package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.*;
import com.example.MovieTicketBooking.entity.*;
import com.example.MovieTicketBooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // In-memory seat locking for real-time availability
    private final Map<String, SeatLock> seatLocks = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lockExpiry = new ConcurrentHashMap<>();

    // Seat lock duration (15 minutes)
    private static final int LOCK_DURATION_MINUTES = 15;

    /**
     * Get available shows for a movie
     */
    @Transactional(readOnly = true)
    public List<ShowResponse> getAvailableShows(Long movieId, Long theaterId, LocalDate showDate) {
        log.info("Fetching available shows for movie: {}, theater: {}, date: {}", movieId, theaterId, showDate);
        
        List<Show> shows;
        if (theaterId != null && showDate != null) {
            shows = showRepository.findByMovieTheaterAndDate(movieId, theaterId, showDate);
        } else if (theaterId != null) {
            shows = showRepository.findByMovieIdAndTheaterIdAndIsActiveTrue(movieId, theaterId);
        } else if (showDate != null) {
            shows = showRepository.findByMovieAndDate(movieId, showDate);
        } else {
            shows = showRepository.findByMovieIdAndIsActiveTrue(movieId);
        }

        return shows.stream()
                .filter(Show::isBookingOpen)
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get seat layout for a show
     */
    @Transactional(readOnly = true)
    public SeatLayoutResponse getSeatLayout(Long showId) {
        log.info("Fetching seat layout for show: {}", showId);
        
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));

        if (!show.isBookingOpen()) {
            throw new RuntimeException("Booking is not open for this show");
        }

        Screen screen = show.getScreen();
        List<Seat> seats = seatRepository.findByScreenIdAndIsActiveTrue(screen.getId());
        
        // Get booked seats
        Set<Long> bookedSeatIds = getBookedSeatIds(showId);
        
        // Get locked seats
        Set<Long> lockedSeatIds = getLockedSeatIds(showId);

        // Group seats by row
        Map<Integer, List<Seat>> seatsByRow = seats.stream()
                .collect(Collectors.groupingBy(Seat::getRowNumber));

        List<SeatLayoutResponse.SeatRowInfo> seatRows = seatsByRow.entrySet().stream()
                .map(entry -> {
                    Integer rowNumber = entry.getKey();
                    List<Seat> rowSeats = entry.getValue();
                    
                    // Sort seats by seat number
                    rowSeats.sort(Comparator.comparing(Seat::getSeatNumber));
                    
                    List<SeatLayoutResponse.SeatInfo> seatInfos = rowSeats.stream()
                            .map(seat -> {
                                boolean isBooked = bookedSeatIds.contains(seat.getId());
                                boolean isLocked = lockedSeatIds.contains(seat.getId());
                                
                                return SeatLayoutResponse.SeatInfo.builder()
                                        .seatId(seat.getId())
                                        .seatIdentifier(seat.getSeatIdentifier())
                                        .seatType(seat.getSeatType().getDisplayName())
                                        .status(isBooked ? "BOOKED" : (isLocked ? "LOCKED" : "AVAILABLE"))
                                        .isActive(seat.getIsActive())
                                        .rowNumber(seat.getRowNumber())
                                        .seatNumber(seat.getSeatNumber())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return SeatLayoutResponse.SeatRowInfo.builder()
                            .rowNumber(rowNumber)
                            .rowLetter(getRowLetter(rowNumber))
                            .seatsPerRow(rowSeats.size())
                            .seatType(rowSeats.get(0).getSeatType().getDisplayName())
                            .hasAisle(false)
                            .aislePosition(null)
                            .seats(seatInfos)
                            .build();
                })
                .sorted(Comparator.comparing(SeatLayoutResponse.SeatRowInfo::getRowNumber))
                .collect(Collectors.toList());

        // Count seat types
        Map<String, Integer> seatTypeCounts = seats.stream()
                .collect(Collectors.groupingBy(
                        seat -> seat.getSeatType().getDisplayName(),
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));

        return SeatLayoutResponse.builder()
                .screenId(screen.getId())
                .screenName(screen.getScreenDisplayName())
                .layoutName("Standard Layout")
                .totalRows(screen.getTotalRows())
                .totalSeatsPerRow(screen.getTotalSeatsPerRow())
                .totalSeats(screen.getTotalSeats())
                .seatRows(seatRows)
                .seatTypeCounts(seatTypeCounts)
                .availableSeats(seats.stream()
                        .filter(seat -> !bookedSeatIds.contains(seat.getId()) && !lockedSeatIds.contains(seat.getId()))
                        .map(Seat::getSeatIdentifier)
                        .collect(Collectors.toList()))
                .bookedSeats(seats.stream()
                        .filter(seat -> bookedSeatIds.contains(seat.getId()))
                        .map(Seat::getSeatIdentifier)
                        .collect(Collectors.toList()))
                .lockedSeats(seats.stream()
                        .filter(seat -> lockedSeatIds.contains(seat.getId()))
                        .map(Seat::getSeatIdentifier)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Lock seats for booking
     */
    public SeatLockResponse lockSeats(Long showId, List<Long> seatIds, Long userId) {
        log.info("Locking seats for show: {}, seats: {}, user: {}", showId, seatIds, userId);
        
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));

        if (!show.isBookingOpen()) {
            throw new RuntimeException("Booking is not open for this show");
        }

        // Validate seats
        List<Seat> seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new RuntimeException("Some seats not found");
        }

        // Check if seats are available
        Set<Long> bookedSeatIds = getBookedSeatIds(showId);
        Set<Long> lockedSeatIds = getLockedSeatIds(showId);
        
        for (Seat seat : seats) {
            if (bookedSeatIds.contains(seat.getId())) {
                throw new RuntimeException("Seat " + seat.getSeatIdentifier() + " is already booked");
            }
            if (lockedSeatIds.contains(seat.getId())) {
                throw new RuntimeException("Seat " + seat.getSeatIdentifier() + " is currently locked by another user");
            }
        }

        // Lock seats
        String lockId = UUID.randomUUID().toString();
        LocalDateTime lockExpiryTime = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
        
        SeatLock seatLock = SeatLock.builder()
                .lockId(lockId)
                .showId(showId)
                .seatIds(new HashSet<>(seatIds))
                .userId(userId)
                .lockedAt(LocalDateTime.now())
                .expiresAt(lockExpiryTime)
                .build();

        seatLocks.put(lockId, seatLock);
        lockExpiry.put(lockId, lockExpiryTime);

        // Calculate total amount
        BigDecimal totalAmount = seats.stream()
                .map(seat -> show.getPriceForSeatType(seat.getSeatType()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SeatLockResponse.builder()
                .lockId(lockId)
                .showId(showId)
                .seatIds(seatIds)
                .totalAmount(totalAmount)
                .expiresAt(lockExpiryTime)
                .build();
    }

    /**
     * Create booking
     */
    public BookingResponse createBooking(CreateBookingRequest request) {
        log.info("Creating booking for user: {}, show: {}, lockId: {}", 
                request.getUserId(), request.getShowId(), request.getLockId());
        
        // Validate seat lock
        SeatLock seatLock = seatLocks.get(request.getLockId());
        if (seatLock == null) {
            throw new RuntimeException("Invalid or expired seat lock");
        }
        
        if (seatLock.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Seat lock has expired");
        }
        
        if (!seatLock.getUserId().equals(request.getUserId())) {
            throw new RuntimeException("Seat lock does not belong to this user");
        }
        
        if (!seatLock.getShowId().equals(request.getShowId())) {
            throw new RuntimeException("Seat lock does not match the show");
        }

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new RuntimeException("Show not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .show(show)
                .totalAmount(request.getTotalAmount())
                .bookingFee(request.getBookingFee())
                .taxAmount(request.getTaxAmount())
                .bookingDate(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusMinutes(30)) // 30 minutes to complete payment
                .build();

        final Booking savedBooking = bookingRepository.save(booking);

        // Create booking seats
        List<Seat> seats = seatRepository.findAllById(seatLock.getSeatIds());
        List<BookingSeat> bookingSeats = seats.stream()
                .map(seat -> {
                    BigDecimal price = show.getPriceForSeatType(seat.getSeatType());
                    return BookingSeat.builder()
                            .booking(savedBooking)
                            .seat(seat)
                            .price(price)
                            .build();
                })
                .collect(Collectors.toList());

        bookingSeatRepository.saveAll(bookingSeats);

        // Remove seat lock
        seatLocks.remove(request.getLockId());
        lockExpiry.remove(request.getLockId());

        return mapToBookingResponse(savedBooking);
    }

    /**
     * Confirm booking (after payment)
     */
    public BookingResponse confirmBooking(Long bookingId, String paymentReference, String paymentMethod) {
        log.info("Confirming booking: {} with payment reference: {}", bookingId, paymentReference);
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.isExpired()) {
            throw new RuntimeException("Booking has expired");
        }

        booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentStatus(Booking.PaymentStatus.COMPLETED);
        booking.setPaymentReference(paymentReference);
        booking.setPaymentMethod(paymentMethod);

        booking = bookingRepository.save(booking);

        // Send confirmation email
        try {
            emailService.sendBookingConfirmation(booking);
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email for booking: {}", booking.getBookingReference(), e);
        }

        return mapToBookingResponse(booking);
    }

    /**
     * Cancel booking
     */
    public BookingResponse cancelBooking(Long bookingId, String reason) {
        log.info("Cancelling booking: {} with reason: {}", bookingId, reason);
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.canBeCancelled()) {
            throw new RuntimeException("Booking cannot be cancelled");
        }

        booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setCancelledAt(LocalDateTime.now());

        booking = bookingRepository.save(booking);

        // Send cancellation email
        try {
            emailService.sendBookingCancellation(booking);
        } catch (Exception e) {
            log.error("Failed to send booking cancellation email for booking: {}", booking.getBookingReference(), e);
        }

        return mapToBookingResponse(booking);
    }

    /**
     * Get user bookings
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId) {
        log.info("Fetching bookings for user: {}", userId);
        
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return bookings.stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get booking by reference
     */
    @Transactional(readOnly = true)
    public BookingResponse getBookingByReference(String bookingReference) {
        log.info("Fetching booking by reference: {}", bookingReference);
        
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        return mapToBookingResponse(booking);
    }

    /**
     * Get booking by ID
     */
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId) {
        log.info("Fetching booking by ID: {}", bookingId);
        
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    /**
     * Get booking by order ID
     */
    @Transactional(readOnly = true)
    public Booking getBookingByOrderId(String orderId) {
        log.info("Fetching booking by order ID: {}", orderId);
        
        return bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Booking not found for order ID: " + orderId));
    }

    /**
     * Get all bookings (Admin only)
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings for admin");
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cleanup expired locks
     */
    public void cleanupExpiredLocks() {
        log.info("Cleaning up expired seat locks");
        
        LocalDateTime now = LocalDateTime.now();
        List<String> expiredLocks = lockExpiry.entrySet().stream()
                .filter(entry -> entry.getValue().isBefore(now))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (String lockId : expiredLocks) {
            seatLocks.remove(lockId);
            lockExpiry.remove(lockId);
        }
    }

    // Helper methods
    private Set<Long> getBookedSeatIds(Long showId) {
        return bookingSeatRepository.findBookedSeatIdsByShowId(showId);
    }

    private Set<Long> getLockedSeatIds(Long showId) {
        // Clean up expired locks first
        cleanupExpiredLocks();
        
        return seatLocks.values().stream()
                .filter(lock -> lock.getShowId().equals(showId))
                .filter(lock -> lock.getExpiresAt().isAfter(LocalDateTime.now())) // Only active locks
                .flatMap(lock -> lock.getSeatIds().stream())
                .collect(Collectors.toSet());
    }

    private ShowResponse mapToShowResponse(Show show) {
        return ShowResponse.builder()
                .id(show.getId())
                .movieId(show.getMovie().getId())
                .movieTitle(show.getMovie().getTitle())
                .theaterId(show.getTheater().getId())
                .theaterName(show.getTheater().getName())
                .screenId(show.getScreen().getId())
                .screenName(show.getScreen().getScreenDisplayName())
                .showDate(show.getShowDate())
                .showTime(show.getShowTime())
                .endTime(show.getEndTime())
                .basePrice(show.getBasePrice())
                .premiumPrice(show.getPremiumPrice())
                .vipPrice(show.getVipPrice())
                .showStatus(show.getShowStatus())
                .isBookingOpen(show.isBookingOpen())
                .build();
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getFirstName() + " " + booking.getUser().getLastName())
                .showId(booking.getShow().getId())
                .movieTitle(booking.getShow().getMovie().getTitle())
                .theaterName(booking.getShow().getTheater().getName())
                .showDateTime(booking.getShow().getShowDateTime())
                .totalAmount(booking.getTotalAmount())
                .bookingFee(booking.getBookingFee())
                .taxAmount(booking.getTaxAmount())
                .bookingStatus(booking.getBookingStatus())
                .paymentStatus(booking.getPaymentStatus())
                .paymentMethod(booking.getPaymentMethod())
                .bookingDate(booking.getBookingDate())
                .expiryTime(booking.getExpiryTime())
                .build();
    }

    /**
     * Get row letter from row number
     */
    private String getRowLetter(int rowNumber) {
        return String.valueOf((char) ('A' + rowNumber - 1));
    }

    // Inner classes for seat locking
    @lombok.Data
    @lombok.Builder
    public static class SeatLock {
        private String lockId;
        private Long showId;
        private Set<Long> seatIds;
        private Long userId;
        private LocalDateTime lockedAt;
        private LocalDateTime expiresAt;
    }
}

package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.*;
import com.example.MovieTicketBooking.entity.Booking;
import com.example.MovieTicketBooking.entity.BookingSeat;
import com.example.MovieTicketBooking.repository.BookingRepository;
import com.example.MovieTicketBooking.repository.BookingSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingHistoryService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;

    /**
     * Get booking history with advanced filtering
     */
    public BookingHistoryResponse getBookingHistory(BookingHistoryRequest request) {
        log.info("Getting booking history for user: {} with filters: {}", request.getUserId(), request);

        // Build specification for filtering
        Specification<Booking> spec = buildBookingSpecification(request);

        // Build pageable for pagination and sorting
        Pageable pageable = buildPageable(request);

        // Execute search
        Page<Booking> bookingPage = bookingRepository.findAll(spec, pageable);

        // Convert to response DTOs
        List<BookingHistoryResponse.BookingHistoryItem> bookingItems = bookingPage.getContent().stream()
                .map(this::mapToBookingHistoryItem)
                .collect(Collectors.toList());

        // Calculate summary
        BookingHistoryResponse.BookingSummary summary = calculateBookingSummary(request.getUserId());

        // Build response
        return BookingHistoryResponse.builder()
                .bookings(bookingItems)
                .totalElements(bookingPage.getTotalElements())
                .totalPages(bookingPage.getTotalPages())
                .currentPage(bookingPage.getNumber())
                .pageSize(bookingPage.getSize())
                .hasNext(bookingPage.hasNext())
                .hasPrevious(bookingPage.hasPrevious())
                .summary(summary)
                .availableStatuses(getAvailableStatuses())
                .availableCities(getAvailableCities())
                .appliedFilters(buildAppliedFilters(request))
                .build();
    }

    /**
     * Get booking history for a specific user
     */
    public BookingHistoryResponse getUserBookingHistory(Long userId, BookingHistoryRequest request) {
        log.info("Getting booking history for user: {}", userId);

        // Set user ID in request
        request.setUserId(userId);
        return getBookingHistory(request);
    }

    /**
     * Get recent bookings for a user
     */
    public BookingHistoryResponse getRecentBookings(Long userId, int limit) {
        log.info("Getting recent bookings for user: {} with limit: {}", userId, limit);

        BookingHistoryRequest request = BookingHistoryRequest.builder()
                .userId(userId)
                .page(0)
                .size(limit)
                .sortBy("bookingDate")
                .sortOrder("DESC")
                .build();

        return getBookingHistory(request);
    }

    /**
     * Get upcoming bookings for a user
     */
    public BookingHistoryResponse getUpcomingBookings(Long userId) {
        log.info("Getting upcoming bookings for user: {}", userId);

        BookingHistoryRequest request = BookingHistoryRequest.builder()
                .userId(userId)
                .showDateFrom(LocalDate.now())
                .bookingStatuses(List.of(Booking.BookingStatus.CONFIRMED, Booking.BookingStatus.PENDING))
                .page(0)
                .size(20)
                .sortBy("showDate")
                .sortOrder("ASC")
                .build();

        return getBookingHistory(request);
    }

    /**
     * Get cancelled bookings for a user
     */
    public BookingHistoryResponse getCancelledBookings(Long userId) {
        log.info("Getting cancelled bookings for user: {}", userId);

        BookingHistoryRequest request = BookingHistoryRequest.builder()
                .userId(userId)
                .bookingStatuses(List.of(Booking.BookingStatus.CANCELLED))
                .page(0)
                .size(20)
                .sortBy("bookingDate")
                .sortOrder("DESC")
                .build();

        return getBookingHistory(request);
    }

    /**
     * Build booking specification for filtering
     */
    private Specification<Booking> buildBookingSpecification(BookingHistoryRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User filter
            if (request.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), request.getUserId()));
            }

            // Booking status filter
            if (request.getBookingStatuses() != null && !request.getBookingStatuses().isEmpty()) {
                predicates.add(root.get("bookingStatus").in(request.getBookingStatuses()));
            }

            // Payment status filter
            if (request.getPaymentStatuses() != null && !request.getPaymentStatuses().isEmpty()) {
                predicates.add(root.get("paymentStatus").in(request.getPaymentStatuses()));
            }

            // Booking date filters
            if (request.getBookingDateFrom() != null) {
                LocalDateTime startOfDay = request.getBookingDateFrom().atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("bookingDate"), startOfDay));
            }

            if (request.getBookingDateTo() != null) {
                LocalDateTime endOfDay = request.getBookingDateTo().atTime(23, 59, 59);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("bookingDate"), endOfDay));
            }

            // Show date filters
            if (request.getShowDateFrom() != null) {
                LocalDateTime startOfDay = request.getShowDateFrom().atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("show").get("showTime"), startOfDay));
            }

            if (request.getShowDateTo() != null) {
                LocalDateTime endOfDay = request.getShowDateTo().atTime(23, 59, 59);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("show").get("showTime"), endOfDay));
            }

            // Movie title filter
            if (request.getMovieTitle() != null && !request.getMovieTitle().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("show").get("movie").get("title")),
                        "%" + request.getMovieTitle().toLowerCase() + "%"
                ));
            }

            // Theater name filter
            if (request.getTheaterName() != null && !request.getTheaterName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("show").get("screen").get("theater").get("name")),
                        "%" + request.getTheaterName().toLowerCase() + "%"
                ));
            }

            // City filter
            if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("show").get("screen").get("theater").get("city")),
                        "%" + request.getCity().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Build pageable for pagination and sorting
     */
    private Pageable buildPageable(BookingHistoryRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;

        Sort sort = buildSort(request);
        return PageRequest.of(page, size, sort);
    }

    /**
     * Build sort specification
     */
    private Sort buildSort(BookingHistoryRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "bookingDate";
        String sortOrder = request.getSortOrder() != null ? request.getSortOrder() : "DESC";

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(direction, sortBy);
    }

    /**
     * Map booking entity to booking history item
     */
    private BookingHistoryResponse.BookingHistoryItem mapToBookingHistoryItem(Booking booking) {
        // Get seat numbers
        List<String> seatNumbers = bookingSeatRepository.findByBookingId(booking.getId())
                .stream()
                .map(bookingSeat -> bookingSeat.getSeat().getSeatIdentifier())
                .collect(Collectors.toList());

        return BookingHistoryResponse.BookingHistoryItem.builder()
                .bookingId(booking.getId())
                .bookingReference(booking.getBookingReference())
                .movieTitle(booking.getShow().getMovie().getTitle())
                .theaterName(booking.getShow().getScreen().getTheater().getName())
                .screenName(booking.getShow().getScreen().getScreenDisplayName())
                .city(booking.getShow().getScreen().getTheater().getCity())
                .showTime(booking.getShow().getShowDate().atTime(booking.getShow().getShowTime()))
                .bookingDate(booking.getBookingDate())
                .bookingStatus(booking.getBookingStatus().name())
                .paymentStatus(booking.getPaymentStatus().name())
                .totalAmount(booking.getTotalAmount())
                .bookingFee(booking.getBookingFee())
                .taxAmount(booking.getTaxAmount())
                .paymentMethod(booking.getPaymentMethod())
                .seatNumbers(seatNumbers)
                .cancellationReason(booking.getCancellationReason())
                .cancelledAt(booking.getCancelledAt())
                .canBeCancelled(booking.canBeCancelled())
                .canBeRefunded(booking.canBeRefunded())
                .build();
    }

    /**
     * Calculate booking summary
     */
    private BookingHistoryResponse.BookingSummary calculateBookingSummary(Long userId) {
        List<Booking> allUserBookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);

        long totalBookings = allUserBookings.size();
        BigDecimal totalAmount = allUserBookings.stream()
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRefunded = allUserBookings.stream()
                .filter(booking -> booking.getBookingStatus() == Booking.BookingStatus.CANCELLED)
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long activeBookings = allUserBookings.stream()
                .filter(booking -> booking.getBookingStatus() == Booking.BookingStatus.CONFIRMED)
                .count();

        long cancelledBookings = allUserBookings.stream()
                .filter(booking -> booking.getBookingStatus() == Booking.BookingStatus.CANCELLED)
                .count();

        long completedBookings = allUserBookings.stream()
                .filter(booking -> booking.getBookingStatus() == Booking.BookingStatus.CONFIRMED)
                .count();

        BigDecimal averageBookingValue = totalBookings > 0 ? 
                totalAmount.divide(BigDecimal.valueOf(totalBookings), 2, java.math.RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;

        return BookingHistoryResponse.BookingSummary.builder()
                .totalBookings(totalBookings)
                .totalAmount(totalAmount)
                .totalRefunded(totalRefunded)
                .activeBookings(activeBookings)
                .cancelledBookings(cancelledBookings)
                .completedBookings(completedBookings)
                .averageBookingValue(averageBookingValue)
                .build();
    }

    /**
     * Get available statuses
     */
    private List<String> getAvailableStatuses() {
        return List.of(
                Booking.BookingStatus.PENDING.name(),
                Booking.BookingStatus.CONFIRMED.name(),
                Booking.BookingStatus.CANCELLED.name(),
                Booking.BookingStatus.EXPIRED.name(),
                Booking.BookingStatus.REFUNDED.name()
        );
    }

    /**
     * Get available cities
     */
    private List<String> getAvailableCities() {
        return bookingRepository.findDistinctCities();
    }

    /**
     * Build applied filters
     */
    private BookingHistoryResponse.BookingHistoryFilters buildAppliedFilters(BookingHistoryRequest request) {
        return BookingHistoryResponse.BookingHistoryFilters.builder()
                .bookingStatuses(request.getBookingStatuses() != null ? 
                        request.getBookingStatuses().stream().map(Enum::name).collect(Collectors.toList()) : null)
                .paymentStatuses(request.getPaymentStatuses() != null ? 
                        request.getPaymentStatuses().stream().map(Enum::name).collect(Collectors.toList()) : null)
                .bookingDateFrom(request.getBookingDateFrom() != null ? request.getBookingDateFrom().toString() : null)
                .bookingDateTo(request.getBookingDateTo() != null ? request.getBookingDateTo().toString() : null)
                .showDateFrom(request.getShowDateFrom() != null ? request.getShowDateFrom().toString() : null)
                .showDateTo(request.getShowDateTo() != null ? request.getShowDateTo().toString() : null)
                .movieTitle(request.getMovieTitle())
                .theaterName(request.getTheaterName())
                .city(request.getCity())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();
    }
}

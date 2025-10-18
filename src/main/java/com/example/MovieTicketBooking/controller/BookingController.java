package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.BookingResponse;
import com.example.MovieTicketBooking.dto.CreateBookingRequest;
import com.example.MovieTicketBooking.dto.SeatLayoutResponse;
import com.example.MovieTicketBooking.dto.SeatLockResponse;
import com.example.MovieTicketBooking.dto.ShowResponse;
import com.example.MovieTicketBooking.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    /**
     * Get available shows for a movie
     */
    @GetMapping("/shows")
    public ResponseEntity<List<ShowResponse>> getAvailableShows(
            @RequestParam Long movieId,
            @RequestParam(required = false) Long theaterId,
            @RequestParam(required = false) LocalDate showDate) {
        try {
            log.info("Fetching available shows for movie: {}, theater: {}, date: {}", movieId, theaterId, showDate);
            List<ShowResponse> shows = bookingService.getAvailableShows(movieId, theaterId, showDate);
            return ResponseEntity.ok(shows);
        } catch (Exception e) {
            log.error("Error fetching available shows", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get seat layout for a show
     */
    @GetMapping("/shows/{showId}/seats")
    public ResponseEntity<SeatLayoutResponse> getSeatLayout(@PathVariable Long showId) {
        try {
            log.info("Fetching seat layout for show: {}", showId);
            SeatLayoutResponse seatLayout = bookingService.getSeatLayout(showId);
            return ResponseEntity.ok(seatLayout);
        } catch (RuntimeException e) {
            log.error("Error fetching seat layout: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching seat layout", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lock seats for booking
     */
    @PostMapping("/seats/lock")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<SeatLockResponse> lockSeats(@Valid @RequestBody LockSeatsRequest request) {
        try {
            log.info("Locking seats for show: {}, seats: {}, user: {}", 
                    request.getShowId(), request.getSeatIds(), request.getUserId());
            SeatLockResponse response = bookingService.lockSeats(
                    request.getShowId(), request.getSeatIds(), request.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error locking seats: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error locking seats", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Create booking
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        try {
            log.info("Creating booking for user: {}, show: {}", request.getUserId(), request.getShowId());
            BookingResponse response = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating booking", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Confirm booking (after payment)
     */
    @PostMapping("/{bookingId}/confirm")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable Long bookingId,
            @RequestParam String paymentReference,
            @RequestParam String paymentMethod) {
        try {
            log.info("Confirming booking: {} with payment reference: {}", bookingId, paymentReference);
            BookingResponse response = bookingService.confirmBooking(bookingId, paymentReference, paymentMethod);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error confirming booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error confirming booking", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Cancel booking
     */
    @PostMapping("/{bookingId}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam String reason) {
        try {
            log.info("Cancelling booking: {} with reason: {}", bookingId, reason);
            BookingResponse response = bookingService.cancelBooking(bookingId, reason);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error cancelling booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error cancelling booking", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user bookings
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@PathVariable Long userId) {
        try {
            log.info("Fetching bookings for user: {}", userId);
            List<BookingResponse> bookings = bookingService.getUserBookings(userId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching user bookings", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get booking by reference
     */
    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<BookingResponse> getBookingByReference(@PathVariable String bookingReference) {
        try {
            log.info("Fetching booking by reference: {}", bookingReference);
            BookingResponse booking = bookingService.getBookingByReference(bookingReference);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            log.error("Booking not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching booking", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all bookings (Admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        try {
            log.info("Fetching all bookings for admin");
            List<BookingResponse> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching all bookings", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Cleanup expired locks (Admin only)
     */
    @PostMapping("/cleanup/locks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cleanupExpiredLocks() {
        try {
            log.info("Cleaning up expired seat locks");
            bookingService.cleanupExpiredLocks();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error cleaning up expired locks", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
// DTO for locking seats
@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class LockSeatsRequest {
    @NotNull(message = "Show ID is required")
    private Long showId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Seat IDs are required")
    private List<Long> seatIds;
}


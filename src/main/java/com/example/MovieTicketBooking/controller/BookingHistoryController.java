package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.BookingHistoryRequest;
import com.example.MovieTicketBooking.dto.BookingHistoryResponse;
import com.example.MovieTicketBooking.entity.User;
import com.example.MovieTicketBooking.service.BookingHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking-history")
@RequiredArgsConstructor
@Slf4j
public class BookingHistoryController {

    private final BookingHistoryService bookingHistoryService;

    /**
     * Get booking history with advanced filtering
     */
    @PostMapping
    public ResponseEntity<BookingHistoryResponse> getBookingHistory(
            @Valid @RequestBody BookingHistoryRequest request,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            request.setUserId(currentUser.getId());
            
            log.info("Getting booking history for user: {}", currentUser.getId());
            BookingHistoryResponse response = bookingHistoryService.getBookingHistory(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting booking history", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get recent bookings for current user
     */
    @GetMapping("/recent")
    public ResponseEntity<BookingHistoryResponse> getRecentBookings(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            log.info("Getting recent bookings for user: {} with limit: {}", currentUser.getId(), limit);
            BookingHistoryResponse response = bookingHistoryService.getRecentBookings(currentUser.getId(), limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting recent bookings", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get upcoming bookings for current user
     */
    @GetMapping("/upcoming")
    public ResponseEntity<BookingHistoryResponse> getUpcomingBookings(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            log.info("Getting upcoming bookings for user: {}", currentUser.getId());
            BookingHistoryResponse response = bookingHistoryService.getUpcomingBookings(currentUser.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting upcoming bookings", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get cancelled bookings for current user
     */
    @GetMapping("/cancelled")
    public ResponseEntity<BookingHistoryResponse> getCancelledBookings(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            log.info("Getting cancelled bookings for user: {}", currentUser.getId());
            BookingHistoryResponse response = bookingHistoryService.getCancelledBookings(currentUser.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting cancelled bookings", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get booking history for a specific user (Admin only)
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<BookingHistoryResponse> getUserBookingHistory(
            @PathVariable Long userId,
            @Valid @RequestBody BookingHistoryRequest request) {
        try {
            log.info("Getting booking history for user: {}", userId);
            BookingHistoryResponse response = bookingHistoryService.getUserBookingHistory(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting user booking history", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.SeatLayoutRequest;
import com.example.MovieTicketBooking.dto.SeatLayoutResponse;
import com.example.MovieTicketBooking.service.SeatLayoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seat-layout")
@RequiredArgsConstructor
@Slf4j
public class SeatLayoutController {

    private final SeatLayoutService seatLayoutService;

    /**
     * Create or update seat layout for a screen
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeatLayoutResponse> createSeatLayout(@Valid @RequestBody SeatLayoutRequest request) {
        try {
            log.info("Creating seat layout for screen: {}", request.getScreenId());
            SeatLayoutResponse response = seatLayoutService.createSeatLayout(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Error creating seat layout: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating seat layout", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get seat layout for a screen
     */
    @GetMapping("/screen/{screenId}")
    public ResponseEntity<SeatLayoutResponse> getSeatLayout(@PathVariable Long screenId) {
        try {
            log.info("Fetching seat layout for screen: {}", screenId);
            SeatLayoutResponse response = seatLayoutService.getSeatLayout(screenId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error fetching seat layout: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching seat layout", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get seat layout for a show
     */
    @GetMapping("/show/{showId}")
    public ResponseEntity<SeatLayoutResponse> getSeatLayoutForShow(@PathVariable Long showId) {
        try {
            log.info("Fetching seat layout for show: {}", showId);
            SeatLayoutResponse response = seatLayoutService.getSeatLayoutForShow(showId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error fetching seat layout for show: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching seat layout for show", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

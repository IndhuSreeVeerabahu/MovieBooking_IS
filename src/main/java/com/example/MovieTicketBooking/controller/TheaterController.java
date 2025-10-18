package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.TheaterRequest;
import com.example.MovieTicketBooking.dto.TheaterResponse;
import com.example.MovieTicketBooking.dto.ScreenResponse;
import com.example.MovieTicketBooking.service.TheaterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TheaterController {

    private final TheaterService theaterService;

    /**
     * Create a new theater (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTheater(@Valid @RequestBody TheaterRequest request) {
        try {
            log.info("Creating new theater: {}", request.getName());
            TheaterResponse response = theaterService.createTheater(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Error creating theater: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("Error creating theater", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred while creating the theater");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get all active theaters (Public)
     */
    @GetMapping
    public ResponseEntity<List<TheaterResponse>> getAllActiveTheaters() {
        try {
            log.info("Fetching all active theaters");
            List<TheaterResponse> theaters = theaterService.getAllActiveTheaters();
            return ResponseEntity.ok(theaters);
        } catch (Exception e) {
            log.error("Error fetching active theaters", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all theaters for admin (including inactive)
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TheaterResponse>> getAllTheatersForAdmin() {
        try {
            log.info("Fetching all theaters for admin");
            List<TheaterResponse> theaters = theaterService.getAllTheaters(0, 1000); // Get all theaters
            return ResponseEntity.ok(theaters);
        } catch (Exception e) {
            log.error("Error fetching all theaters for admin", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all cities from theaters
     */
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        try {
            log.info("Fetching all cities from theaters");
            List<String> cities = theaterService.getAllCities();
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            log.error("Error fetching cities", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get theaters by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<TheaterResponse>> getTheatersByCity(@PathVariable String city) {
        try {
            log.info("Fetching theaters for city: {}", city);
            List<TheaterResponse> theaters = theaterService.getTheatersByCity(city);
            return ResponseEntity.ok(theaters);
        } catch (Exception e) {
            log.error("Error fetching theaters by city", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get theater by ID
     */
    @GetMapping("/{theaterId}")
    public ResponseEntity<TheaterResponse> getTheaterById(@PathVariable Long theaterId) {
        try {
            log.info("Fetching theater by ID: {}", theaterId);
            TheaterResponse theater = theaterService.getTheaterById(theaterId);
            return ResponseEntity.ok(theater);
        } catch (RuntimeException e) {
            log.error("Theater not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching theater", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get screens for a theater
     */
    @GetMapping("/{theaterId}/screens")
    public ResponseEntity<List<ScreenResponse>> getTheaterScreens(@PathVariable Long theaterId) {
        try {
            log.info("Fetching screens for theater: {}", theaterId);
            List<ScreenResponse> screens = theaterService.getTheaterScreens(theaterId);
            return ResponseEntity.ok(screens);
        } catch (RuntimeException e) {
            log.error("Error fetching theater screens: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching theater screens", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update theater (Admin only)
     */
    @PutMapping("/{theaterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTheater(
            @PathVariable Long theaterId,
            @Valid @RequestBody TheaterRequest request) {
        try {
            log.info("Updating theater: {} with data: {}", theaterId, request);
            TheaterResponse response = theaterService.updateTheater(theaterId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating theater: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("Error updating theater", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred while updating the theater");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Toggle theater status (Admin only)
     */
    @PutMapping("/{theaterId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TheaterResponse> toggleTheaterStatus(@PathVariable Long theaterId) {
        try {
            log.info("Toggling theater status: {}", theaterId);
            TheaterResponse response = theaterService.toggleTheaterStatus(theaterId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error toggling theater status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error toggling theater status", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete theater (Admin only)
     */
    @DeleteMapping("/{theaterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteTheater(@PathVariable Long theaterId) {
        try {
            log.info("Deleting theater: {}", theaterId);
            theaterService.deleteTheater(theaterId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting theater: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("Error deleting theater", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred while deleting the theater");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get all theaters with pagination (Admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TheaterResponse>> getAllTheaters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Fetching all theaters - page: {}, size: {}", page, size);
            List<TheaterResponse> theaters = theaterService.getAllTheaters(page, size);
            return ResponseEntity.ok(theaters);
        } catch (Exception e) {
            log.error("Error fetching all theaters", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Check if theater can be deleted (Admin only)
     */
    @GetMapping("/{theaterId}/can-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> canDeleteTheater(@PathVariable Long theaterId) {
        try {
            log.info("Checking if theater can be deleted: {}", theaterId);
            Map<String, Object> result = theaterService.checkTheaterDeletionStatus(theaterId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Error checking theater deletion status: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("canDelete", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("Error checking theater deletion status", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("canDelete", false);
            errorResponse.put("message", "An unexpected error occurred");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}

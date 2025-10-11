package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.ShowRequest;
import com.example.MovieTicketBooking.dto.ShowResponse;
import com.example.MovieTicketBooking.entity.Show;
import com.example.MovieTicketBooking.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ShowController {

    private final ShowService showService;

    /**
     * Create a new show (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody ShowRequest request) {
        try {
            log.info("Creating new show for movie: {} at theater: {}", request.getMovieId(), request.getTheaterId());
            ShowResponse response = showService.createShow(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating show", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get available shows for a movie
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowResponse>> getShowsByMovie(@PathVariable Long movieId) {
        try {
            log.info("Fetching shows for movie: {}", movieId);
            List<ShowResponse> shows = showService.getShowsByMovie(movieId);
            return ResponseEntity.ok(shows);
        } catch (Exception e) {
            log.error("Error fetching shows by movie", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get shows by theater
     */
    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<ShowResponse>> getShowsByTheater(@PathVariable Long theaterId) {
        try {
            log.info("Fetching shows for theater: {}", theaterId);
            List<ShowResponse> shows = showService.getShowsByTheater(theaterId);
            return ResponseEntity.ok(shows);
        } catch (Exception e) {
            log.error("Error fetching shows by theater", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get shows by movie and theater
     */
    @GetMapping("/movie/{movieId}/theater/{theaterId}")
    public ResponseEntity<List<ShowResponse>> getShowsByMovieAndTheater(
            @PathVariable Long movieId,
            @PathVariable Long theaterId) {
        try {
            log.info("Fetching shows for movie: {} at theater: {}", movieId, theaterId);
            List<ShowResponse> shows = showService.getShowsByMovieAndTheater(movieId, theaterId);
            return ResponseEntity.ok(shows);
        } catch (Exception e) {
            log.error("Error fetching shows by movie and theater", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get shows by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<ShowResponse>> getShowsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        try {
            log.info("Fetching shows from {} to {}", startDate, endDate);
            List<ShowResponse> shows = showService.getShowsByDateRange(startDate, endDate);
            return ResponseEntity.ok(shows);
        } catch (Exception e) {
            log.error("Error fetching shows by date range", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get show by ID
     */
    @GetMapping("/{showId}")
    public ResponseEntity<ShowResponse> getShowById(@PathVariable Long showId) {
        try {
            log.info("Fetching show by ID: {}", showId);
            ShowResponse show = showService.getShowById(showId);
            return ResponseEntity.ok(show);
        } catch (RuntimeException e) {
            log.error("Show not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching show", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update show (Admin only)
     */
    @PutMapping("/{showId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowResponse> updateShow(
            @PathVariable Long showId,
            @Valid @RequestBody ShowRequest request) {
        try {
            log.info("Updating show: {}", showId);
            ShowResponse response = showService.updateShow(showId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating show: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating show", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Toggle show status (Admin only)
     */
    @PutMapping("/{showId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowResponse> toggleShowStatus(@PathVariable Long showId) {
        try {
            log.info("Toggling show status: {}", showId);
            ShowResponse response = showService.toggleShowStatus(showId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error toggling show status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error toggling show status", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update show status (Admin only)
     */
    @PutMapping("/{showId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowResponse> updateShowStatus(
            @PathVariable Long showId,
            @RequestParam String status) {
        try {
            log.info("Updating show status: {} to {}", showId, status);
            Show.ShowStatus showStatus = Show.ShowStatus.valueOf(status.toUpperCase());
            ShowResponse response = showService.updateShowStatus(showId, showStatus);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid show status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Error updating show status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating show status", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete show (Admin only)
     */
    @DeleteMapping("/{showId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShow(@PathVariable Long showId) {
        try {
            log.info("Deleting show: {}", showId);
            showService.deleteShow(showId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting show: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error deleting show", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all shows with pagination (Admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShowResponse>> getAllShows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Fetching all shows - page: {}, size: {}", page, size);
            List<ShowResponse> shows = showService.getAllShows(page, size);
            return ResponseEntity.ok(shows);
        } catch (Exception e) {
            log.error("Error fetching all shows", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all shows without pagination (Admin only)
     */
    @GetMapping("/admin/all-shows")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShowResponse>> getAllShowsForAdmin() {
        try {
            log.info("Fetching all shows for admin");
            List<ShowResponse> shows = showService.getAllShowsForAdmin();
            return ResponseEntity.ok(shows);
        } catch (Exception e) {
            log.error("Error fetching all shows for admin", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

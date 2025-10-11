package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.MovieRequest;
import com.example.MovieTicketBooking.dto.MovieResponse;
import com.example.MovieTicketBooking.entity.Movie;
import com.example.MovieTicketBooking.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService movieService;

    /**
     * Create a new movie (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        try {
            log.info("Creating new movie: {}", request.getTitle());
            MovieResponse response = movieService.createMovie(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating movie", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all active movies (Public)
     */
    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllActiveMovies() {
        try {
            log.info("Fetching all active movies");
            List<MovieResponse> movies = movieService.getAllActiveMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            log.error("Error fetching active movies", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all movies with pagination (Admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MovieResponse>> getAllMoviesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            log.info("Fetching all movies with pagination");
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<MovieResponse> movies = movieService.getMoviesPaginated(pageable);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            log.error("Error fetching movies with pagination", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get movie by ID (Public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        try {
            log.info("Fetching movie with ID: {}", id);
            MovieResponse movie = movieService.getMovieById(id);
            return ResponseEntity.ok(movie);
        } catch (RuntimeException e) {
            log.error("Movie not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching movie with ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search movies (Public)
     */
    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Movie.Genre genre,
            @RequestParam(required = false) Movie.Rating rating,
            @RequestParam(required = false) String language) {
        try {
            log.info("Searching movies with criteria");
            List<MovieResponse> movies = movieService.searchMovies(title, genre, rating, language);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            log.error("Error searching movies", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get movies by genre (Public)
     */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieResponse>> getMoviesByGenre(@PathVariable Movie.Genre genre) {
        try {
            log.info("Fetching movies by genre: {}", genre);
            List<MovieResponse> movies = movieService.getMoviesByGenre(genre);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            log.error("Error fetching movies by genre: {}", genre, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get movies by rating (Public)
     */
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<MovieResponse>> getMoviesByRating(@PathVariable Movie.Rating rating) {
        try {
            log.info("Fetching movies by rating: {}", rating);
            List<MovieResponse> movies = movieService.getMoviesByRating(rating);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            log.error("Error fetching movies by rating: {}", rating, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get featured movies (Public)
     */
    @GetMapping("/featured")
    public ResponseEntity<List<MovieResponse>> getFeaturedMovies() {
        try {
            log.info("Fetching featured movies");
            List<MovieResponse> movies = movieService.getFeaturedMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            log.error("Error fetching featured movies", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get currently showing movies (Public)
     */
    @GetMapping("/currently-showing")
    public ResponseEntity<List<MovieResponse>> getCurrentlyShowingMovies() {
        try {
            log.info("Fetching currently showing movies");
            List<MovieResponse> movies = movieService.getCurrentlyShowingMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            log.error("Error fetching currently showing movies", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get coming soon movies (Public)
     */
    @GetMapping("/coming-soon")
    public ResponseEntity<List<MovieResponse>> getComingSoonMovies() {
        try {
            log.info("Fetching coming soon movies");
            List<MovieResponse> movies = movieService.getComingSoonMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            log.error("Error fetching coming soon movies", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get movies by date range (Public)
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<MovieResponse>> getMoviesByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        try {
            log.info("Fetching movies by date range");
            List<MovieResponse> movies = movieService.getMoviesByDateRange(startDate, endDate);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            log.error("Error fetching movies by date range", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update movie (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id, 
                                                    @Valid @RequestBody MovieRequest request) {
        try {
            log.info("Updating movie with ID: {}", id);
            MovieResponse response = movieService.updateMovie(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Movie not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating movie with ID: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Toggle movie active status (Admin only)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> toggleMovieStatus(@PathVariable Long id) {
        try {
            log.info("Toggling movie status for ID: {}", id);
            MovieResponse response = movieService.toggleMovieStatus(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Movie not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error toggling movie status for ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Toggle movie featured status (Admin only)
     */
    @PatchMapping("/{id}/toggle-featured")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> toggleMovieFeatured(@PathVariable Long id) {
        try {
            log.info("Toggling movie featured status for ID: {}", id);
            MovieResponse response = movieService.toggleMovieFeatured(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Movie not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error toggling movie featured status for ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete movie (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        try {
            log.info("Deleting movie with ID: {}", id);
            movieService.deleteMovie(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Movie not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting movie with ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get movie statistics (Admin only)
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieService.MovieStats> getMovieStats() {
        try {
            log.info("Fetching movie statistics");
            MovieService.MovieStats stats = movieService.getMovieStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching movie statistics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all genres (Public)
     */
    @GetMapping("/genres")
    public ResponseEntity<Movie.Genre[]> getAllGenres() {
        return ResponseEntity.ok(Movie.Genre.values());
    }

    /**
     * Get all ratings (Public)
     */
    @GetMapping("/ratings")
    public ResponseEntity<Movie.Rating[]> getAllRatings() {
        return ResponseEntity.ok(Movie.Rating.values());
    }
}

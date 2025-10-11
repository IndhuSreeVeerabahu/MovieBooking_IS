package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.MovieSearchRequest;
import com.example.MovieTicketBooking.dto.MovieSearchResponse;
import com.example.MovieTicketBooking.service.MovieSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/search")
@RequiredArgsConstructor
@Slf4j
public class MovieSearchController {

    private final MovieSearchService movieSearchService;

    /**
     * Advanced movie search with filtering
     */
    @PostMapping
    public ResponseEntity<MovieSearchResponse> searchMovies(@Valid @RequestBody MovieSearchRequest request) {
        try {
            log.info("Performing movie search with filters: {}", request);
            MovieSearchResponse response = movieSearchService.searchMovies(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching movies", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get movies by location
     */
    @GetMapping("/location/{city}")
    public ResponseEntity<MovieSearchResponse> getMoviesByLocation(@PathVariable String city) {
        try {
            log.info("Getting movies for city: {}", city);
            MovieSearchResponse response = movieSearchService.getMoviesByLocation(city);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting movies by location", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get movies by theater
     */
    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<MovieSearchResponse> getMoviesByTheater(@PathVariable Long theaterId) {
        try {
            log.info("Getting movies for theater: {}", theaterId);
            MovieSearchResponse response = movieSearchService.getMoviesByTheater(theaterId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting movies by theater", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get featured movies
     */
    @GetMapping("/featured")
    public ResponseEntity<MovieSearchResponse> getFeaturedMovies() {
        try {
            log.info("Getting featured movies");
            MovieSearchResponse response = movieSearchService.getFeaturedMovies();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting featured movies", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get currently showing movies
     */
    @GetMapping("/currently-showing")
    public ResponseEntity<MovieSearchResponse> getCurrentlyShowingMovies() {
        try {
            log.info("Getting currently showing movies");
            MovieSearchResponse response = movieSearchService.getCurrentlyShowingMovies();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting currently showing movies", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Quick search by title
     */
    @GetMapping("/quick")
    public ResponseEntity<MovieSearchResponse> quickSearch(@RequestParam String query) {
        try {
            log.info("Performing quick search for: {}", query);
            MovieSearchRequest request = MovieSearchRequest.builder()
                    .title(query)
                    .isActive(true)
                    .page(0)
                    .size(10)
                    .sortBy("title")
                    .sortOrder("ASC")
                    .build();
            
            MovieSearchResponse response = movieSearchService.searchMovies(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error performing quick search", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.ShowtimeSearchRequest;
import com.example.MovieTicketBooking.dto.ShowtimeSearchResponse;
import com.example.MovieTicketBooking.service.ShowtimeSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/showtimes/search")
@RequiredArgsConstructor
@Slf4j
public class ShowtimeSearchController {

    private final ShowtimeSearchService showtimeSearchService;

    /**
     * Advanced showtime search with filtering
     */
    @PostMapping
    public ResponseEntity<ShowtimeSearchResponse> searchShowtimes(@Valid @RequestBody ShowtimeSearchRequest request) {
        try {
            log.info("Performing showtime search with filters: {}", request);
            ShowtimeSearchResponse response = showtimeSearchService.searchShowtimes(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching showtimes", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get showtimes for a specific movie
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ShowtimeSearchResponse> getShowtimesForMovie(
            @PathVariable Long movieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate) {
        try {
            log.info("Getting showtimes for movie: {} on date: {}", movieId, showDate);
            ShowtimeSearchResponse response = showtimeSearchService.getShowtimesForMovie(
                    movieId, showDate != null ? showDate : LocalDate.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting showtimes for movie", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get showtimes for a specific theater
     */
    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<ShowtimeSearchResponse> getShowtimesForTheater(
            @PathVariable Long theaterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate) {
        try {
            log.info("Getting showtimes for theater: {} on date: {}", theaterId, showDate);
            ShowtimeSearchResponse response = showtimeSearchService.getShowtimesForTheater(
                    theaterId, showDate != null ? showDate : LocalDate.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting showtimes for theater", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get showtimes by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<ShowtimeSearchResponse> getShowtimesByCity(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate) {
        try {
            log.info("Getting showtimes for city: {} on date: {}", city, showDate);
            ShowtimeSearchResponse response = showtimeSearchService.getShowtimesByCity(
                    city, showDate != null ? showDate : LocalDate.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting showtimes by city", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get showtimes by time range
     */
    @GetMapping("/time-range")
    public ResponseEntity<ShowtimeSearchResponse> getShowtimesByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        try {
            log.info("Getting showtimes for date: {} between {} and {}", showDate, startTime, endTime);
            ShowtimeSearchResponse response = showtimeSearchService.getShowtimesByTimeRange(
                    showDate, 
                    startTime != null ? startTime : LocalTime.of(9, 0),
                    endTime != null ? endTime : LocalTime.of(23, 0));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting showtimes by time range", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get today's showtimes
     */
    @GetMapping("/today")
    public ResponseEntity<ShowtimeSearchResponse> getTodaysShowtimes() {
        try {
            log.info("Getting today's showtimes");
            ShowtimeSearchRequest request = ShowtimeSearchRequest.builder()
                    .showDate(LocalDate.now())
                    .isActive(true)
                    .page(0)
                    .size(50)
                    .sortBy("showTime")
                    .sortOrder("ASC")
                    .build();
            
            ShowtimeSearchResponse response = showtimeSearchService.searchShowtimes(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting today's showtimes", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.TheaterSearchRequest;
import com.example.MovieTicketBooking.dto.TheaterSearchResponse;
import com.example.MovieTicketBooking.service.TheaterSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters/search")
@RequiredArgsConstructor
@Slf4j
public class TheaterSearchController {

    private final TheaterSearchService theaterSearchService;

    /**
     * Advanced theater search with filtering
     */
    @PostMapping
    public ResponseEntity<TheaterSearchResponse> searchTheaters(@Valid @RequestBody TheaterSearchRequest request) {
        try {
            log.info("Performing theater search with filters: {}", request);
            TheaterSearchResponse response = theaterSearchService.searchTheaters(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching theaters", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get theaters by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<TheaterSearchResponse> getTheatersByCity(@PathVariable String city) {
        try {
            log.info("Getting theaters for city: {}", city);
            TheaterSearchResponse response = theaterSearchService.getTheatersByCity(city);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting theaters by city", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get theaters by state
     */
    @GetMapping("/state/{state}")
    public ResponseEntity<TheaterSearchResponse> getTheatersByState(@PathVariable String state) {
        try {
            log.info("Getting theaters for state: {}", state);
            TheaterSearchResponse response = theaterSearchService.getTheatersByState(state);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting theaters by state", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get theaters by location
     */
    @GetMapping("/location")
    public ResponseEntity<TheaterSearchResponse> getTheatersByLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radius) {
        try {
            log.info("Getting theaters near location: {}, {} within {} km", latitude, longitude, radius);
            TheaterSearchResponse response = theaterSearchService.getTheatersByLocation(latitude, longitude, radius);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting theaters by location", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get theaters by amenities
     */
    @GetMapping("/amenities")
    public ResponseEntity<TheaterSearchResponse> getTheatersByAmenities(@RequestParam List<String> amenities) {
        try {
            log.info("Getting theaters with amenities: {}", amenities);
            TheaterSearchResponse response = theaterSearchService.getTheatersByAmenities(amenities);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting theaters by amenities", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Quick search by theater name
     */
    @GetMapping("/quick")
    public ResponseEntity<TheaterSearchResponse> quickSearch(@RequestParam String query) {
        try {
            log.info("Performing quick theater search for: {}", query);
            TheaterSearchRequest request = TheaterSearchRequest.builder()
                    .name(query)
                    .isActive(true)
                    .page(0)
                    .size(10)
                    .sortBy("name")
                    .sortOrder("ASC")
                    .build();
            
            TheaterSearchResponse response = theaterSearchService.searchTheaters(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error performing quick theater search", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

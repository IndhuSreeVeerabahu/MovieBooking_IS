package com.example.MovieTicketBooking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP");
    }

    @GetMapping("/api/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Movie Ticket Booking System is running");
    }
}

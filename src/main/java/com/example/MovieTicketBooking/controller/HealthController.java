package com.example.MovieTicketBooking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "MovieTicketBooking");
        health.put("version", "0.0.1-SNAPSHOT");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> info = new HashMap<>();
        info.put("message", "Movie Ticket Booking System API");
        info.put("status", "UP");
        info.put("timestamp", LocalDateTime.now());
        info.put("version", "0.0.1-SNAPSHOT");
        return ResponseEntity.ok(info);
    }
}

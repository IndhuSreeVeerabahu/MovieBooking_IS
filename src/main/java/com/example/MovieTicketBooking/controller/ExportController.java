package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/export")
@PreAuthorize("hasRole('ADMIN')")
public class ExportController {

    @Autowired
    private ExportService exportService;

    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @GetMapping("/users/excel")
    public ResponseEntity<byte[]> exportUsersToExcel() {
        try {
            byte[] data = exportService.exportUsersToExcel();
            String filename = "users_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(data.length);
            
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/csv")
    public ResponseEntity<String> exportUsersToCSV() {
        try {
            String csvData = exportService.exportUsersToCSV();
            String filename = "users_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/movies/excel")
    public ResponseEntity<byte[]> exportMoviesToExcel() {
        try {
            byte[] data = exportService.exportMoviesToExcel();
            String filename = "movies_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(data.length);
            
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/movies/csv")
    public ResponseEntity<String> exportMoviesToCSV() {
        try {
            String csvData = exportService.exportMoviesToCSV();
            String filename = "movies_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/bookings/excel")
    public ResponseEntity<byte[]> exportBookingsToExcel() {
        try {
            byte[] data = exportService.exportBookingsToExcel();
            String filename = "bookings_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(data.length);
            
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/bookings/csv")
    public ResponseEntity<String> exportBookingsToCSV() {
        try {
            String csvData = exportService.exportBookingsToCSV();
            String filename = "bookings_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/theaters/excel")
    public ResponseEntity<byte[]> exportTheatersToExcel() {
        try {
            byte[] data = exportService.exportTheatersToExcel();
            String filename = "theaters_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(data.length);
            
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/theaters/csv")
    public ResponseEntity<String> exportTheatersToCSV() {
        try {
            String csvData = exportService.exportTheatersToCSV();
            String filename = "theaters_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/shows/excel")
    public ResponseEntity<byte[]> exportShowsToExcel() {
        try {
            byte[] data = exportService.exportShowsToExcel();
            String filename = "shows_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(data.length);
            
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/shows/csv")
    public ResponseEntity<String> exportShowsToCSV() {
        try {
            String csvData = exportService.exportShowsToCSV();
            String filename = "shows_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

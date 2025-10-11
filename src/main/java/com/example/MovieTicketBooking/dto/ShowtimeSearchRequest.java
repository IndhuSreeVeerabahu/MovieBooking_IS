package com.example.MovieTicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeSearchRequest {

    private Long movieId;
    private Long theaterId;
    private Long screenId;
    private LocalDate showDate;
    private LocalTime showTimeFrom;
    private LocalTime showTimeTo;
    private List<String> screenTypes; // STANDARD, PREMIUM, IMAX, etc.
    private List<String> languages;
    private Boolean isActive;
    private String sortBy; // showTime, screenName, price
    private String sortOrder; // ASC, DESC
    private Integer page;
    private Integer size;
    private String city; // For location-based search
}

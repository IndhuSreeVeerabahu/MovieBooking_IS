package com.example.MovieTicketBooking.dto;

import com.example.MovieTicketBooking.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSearchRequest {

    private String title;
    private List<Movie.Genre> genres;
    private List<Movie.Rating> ratings;
    private List<String> languages;
    private String director;
    private String cast;
    private LocalDateTime releaseDateFrom;
    private LocalDateTime releaseDateTo;
    private Boolean isActive;
    private Boolean isFeatured;
    private String sortBy; // title, releaseDate, rating, duration
    private String sortOrder; // ASC, DESC
    private Integer page;
    private Integer size;
    private String city; // For location-based search
    private Long theaterId; // For theater-specific search
}

package com.example.MovieTicketBooking.dto;

import com.example.MovieTicketBooking.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {

    private Long id;
    private String title;
    private String description;
    private Movie.Genre genre;
    private Integer durationMinutes;
    private String formattedDuration;
    private Movie.Rating rating;
    private String posterUrl;
    private LocalDateTime releaseDate;
    private LocalDateTime endDate;
    private String director;
    private String cast;
    private String language;
    private Boolean isActive;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Status indicators
    private Boolean isCurrentlyShowing;
    private Boolean isComingSoon;
    private Boolean isEnded;

    public static MovieResponse fromMovie(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .genre(movie.getGenre())
                .durationMinutes(movie.getDurationMinutes())
                .formattedDuration(movie.getFormattedDuration())
                .rating(movie.getRating())
                .posterUrl(movie.getPosterUrl())
                .releaseDate(movie.getReleaseDate())
                .endDate(movie.getEndDate())
                .director(movie.getDirector())
                .cast(movie.getCast())
                .language(movie.getLanguage())
                .isActive(movie.getIsActive())
                .isFeatured(movie.getIsFeatured())
                .createdAt(movie.getCreatedAt())
                .updatedAt(movie.getUpdatedAt())
                .isCurrentlyShowing(movie.isCurrentlyShowing())
                .isComingSoon(movie.isComingSoon())
                .isEnded(movie.isEnded())
                .build();
    }
}

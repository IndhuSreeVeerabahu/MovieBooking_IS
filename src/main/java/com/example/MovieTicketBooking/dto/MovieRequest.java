package com.example.MovieTicketBooking.dto;

import com.example.MovieTicketBooking.entity.Movie;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {

    @NotBlank(message = "Movie title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Genre is required")
    private Movie.Genre genre;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration must not exceed 600 minutes")
    private Integer durationMinutes;

    @NotNull(message = "Rating is required")
    private Movie.Rating rating;

    @Size(max = 500, message = "Poster URL must not exceed 500 characters")
    private String posterUrl;


    private LocalDateTime releaseDate;

    private LocalDateTime endDate;

    @Size(max = 255, message = "Director name must not exceed 255 characters")
    private String director;

    @Size(max = 1000, message = "Cast information must not exceed 1000 characters")
    private String cast;

    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isFeatured = false;
}

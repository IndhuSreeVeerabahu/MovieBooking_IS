package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.MovieRequest;
import com.example.MovieTicketBooking.dto.MovieResponse;
import com.example.MovieTicketBooking.entity.Movie;
import com.example.MovieTicketBooking.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;

    /**
     * Create a new movie
     */
    public MovieResponse createMovie(MovieRequest request) {
        log.info("Creating new movie: {}", request.getTitle());

        // Check if movie with same title already exists
        if (movieRepository.existsByTitleIgnoreCaseAndIsActiveTrue(request.getTitle(), null)) {
            throw new RuntimeException("Movie with title '" + request.getTitle() + "' already exists");
        }

        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .genre(request.getGenre())
                .durationMinutes(request.getDurationMinutes())
                .rating(request.getRating())
                .posterUrl(request.getPosterUrl())
                .releaseDate(request.getReleaseDate())
                .endDate(request.getEndDate())
                .director(request.getDirector())
                .cast(request.getCast())
                .language(request.getLanguage())
                .isActive(request.getIsActive())
                .isFeatured(request.getIsFeatured())
                .build();

        movie = movieRepository.save(movie);
        log.info("Movie created successfully with ID: {}", movie.getId());

        return MovieResponse.fromMovie(movie);
    }

    /**
     * Get movie by ID
     */
    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        log.info("Fetching movie with ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
        return MovieResponse.fromMovie(movie);
    }

    /**
     * Get all active movies
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getAllActiveMovies() {
        log.info("Fetching all active movies");
        return movieRepository.findByIsActiveTrue().stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    /**
     * Get all movies (including inactive) - Admin only
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getAllMovies() {
        log.info("Fetching all movies");
        return movieRepository.findAll().stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    /**
     * Get movies with pagination (only active movies)
     */
    @Transactional(readOnly = true)
    public Page<MovieResponse> getMoviesPaginated(Pageable pageable) {
        log.info("Fetching active movies with pagination");
        return movieRepository.findByIsActiveTrue(pageable)
                .map(MovieResponse::fromMovie);
    }

    /**
     * Search movies by criteria
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> searchMovies(String title, Movie.Genre genre, Movie.Rating rating, String language) {
        log.info("Searching movies with criteria - title: {}, genre: {}, rating: {}, language: {}", 
                title, genre, rating, language);
        return movieRepository.searchMovies(title, genre, rating, language).stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    /**
     * Search movies with pagination
     */
    @Transactional(readOnly = true)
    public Page<MovieResponse> searchMoviesPaginated(String title, Movie.Genre genre, Movie.Rating rating, 
                                                     String language, Pageable pageable) {
        log.info("Searching movies with pagination");
        return movieRepository.searchMoviesPaginated(title, genre, rating, language, pageable)
                .map(MovieResponse::fromMovie);
    }

    /**
     * Get movies by genre
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getMoviesByGenre(Movie.Genre genre) {
        log.info("Fetching movies by genre: {}", genre);
        return movieRepository.findByGenreAndIsActiveTrue(genre).stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    /**
     * Get movies by rating
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getMoviesByRating(Movie.Rating rating) {
        log.info("Fetching movies by rating: {}", rating);
        return movieRepository.findByRatingAndIsActiveTrue(rating).stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    /**
     * Get featured movies
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getFeaturedMovies() {
        log.info("Fetching featured movies");
        return movieRepository.findByIsFeaturedTrueAndIsActiveTrue().stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    /**
     * Get currently showing movies
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getCurrentlyShowingMovies() {
        log.info("Fetching currently showing movies");
        return movieRepository.findCurrentlyShowingMovies(LocalDateTime.now()).stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    /**
     * Get coming soon movies
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getComingSoonMovies() {
        log.info("Fetching coming soon movies");
        return movieRepository.findComingSoonMovies(LocalDateTime.now()).stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    /**
     * Get movies by date range
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getMoviesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching movies by date range: {} to {}", startDate, endDate);
        return movieRepository.findMoviesByDateRange(startDate, endDate).stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    /**
     * Update movie
     */
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        log.info("Updating movie with ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));

        // Check if title is being changed and if new title already exists
        if (!movie.getTitle().equalsIgnoreCase(request.getTitle()) && 
            movieRepository.existsByTitleIgnoreCaseAndIsActiveTrue(request.getTitle(), id)) {
            throw new RuntimeException("Movie with title '" + request.getTitle() + "' already exists");
        }

        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setGenre(request.getGenre());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setRating(request.getRating());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setEndDate(request.getEndDate());
        movie.setDirector(request.getDirector());
        movie.setCast(request.getCast());
        movie.setLanguage(request.getLanguage());
        movie.setIsActive(request.getIsActive());
        movie.setIsFeatured(request.getIsFeatured());

        movie = movieRepository.save(movie);
        log.info("Movie updated successfully with ID: {}", movie.getId());

        return MovieResponse.fromMovie(movie);
    }

    /**
     * Toggle movie active status
     */
    public MovieResponse toggleMovieStatus(Long id) {
        log.info("Toggling movie status for ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
        
        movie.setIsActive(!movie.getIsActive());
        movie = movieRepository.save(movie);
        log.info("Movie status toggled for ID: {}", movie.getId());
        
        return MovieResponse.fromMovie(movie);
    }

    /**
     * Toggle movie featured status
     */
    public MovieResponse toggleMovieFeatured(Long id) {
        log.info("Toggling movie featured status for ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
        
        movie.setIsFeatured(!movie.getIsFeatured());
        movie = movieRepository.save(movie);
        log.info("Movie featured status toggled for ID: {}", movie.getId());
        
        return MovieResponse.fromMovie(movie);
    }

    /**
     * Delete movie (soft delete by setting isActive to false)
     */
    public void deleteMovie(Long id) {
        log.info("Deleting movie with ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
        
        movie.setIsActive(false);
        movieRepository.save(movie);
        log.info("Movie deleted successfully with ID: {}", id);
    }

    /**
     * Permanently delete movie from database
     */
    public void permanentDeleteMovie(Long id) {
        log.info("Permanently deleting movie with ID: {}", id);
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found with ID: " + id);
        }
        movieRepository.deleteById(id);
        log.info("Movie permanently deleted with ID: {}", id);
    }

    /**
     * Get movie statistics
     */
    @Transactional(readOnly = true)
    public MovieStats getMovieStats() {
        log.info("Fetching movie statistics");
        long totalMovies = movieRepository.count();
        long activeMovies = movieRepository.findByIsActiveTrue().size();
        long featuredMovies = movieRepository.findByIsFeaturedTrueAndIsActiveTrue().size();
        long currentlyShowing = movieRepository.findCurrentlyShowingMovies(LocalDateTime.now()).size();
        long comingSoon = movieRepository.findComingSoonMovies(LocalDateTime.now()).size();
        
        return MovieStats.builder()
                .totalMovies(totalMovies)
                .activeMovies(activeMovies)
                .inactiveMovies(totalMovies - activeMovies)
                .featuredMovies(featuredMovies)
                .currentlyShowing(currentlyShowing)
                .comingSoon(comingSoon)
                .build();
    }

    /**
     * Movie statistics DTO
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MovieStats {
        private Long totalMovies;
        private Long activeMovies;
        private Long inactiveMovies;
        private Long featuredMovies;
        private Long currentlyShowing;
        private Long comingSoon;
    }
}

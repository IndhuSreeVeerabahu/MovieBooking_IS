package com.example.MovieTicketBooking.repository;

import com.example.MovieTicketBooking.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    // Find movies by genre
    List<Movie> findByGenreAndIsActiveTrue(Movie.Genre genre);
    
    // Find movies by rating
    List<Movie> findByRatingAndIsActiveTrue(Movie.Rating rating);
    
    // Find active movies
    List<Movie> findByIsActiveTrue();
    
    // Find active movies with pagination
    Page<Movie> findByIsActiveTrue(Pageable pageable);
    
    // Find featured movies
    List<Movie> findByIsFeaturedTrueAndIsActiveTrue();
    
    // Find movies by title (case insensitive)
    List<Movie> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);
    
    // Find movies by director
    List<Movie> findByDirectorContainingIgnoreCaseAndIsActiveTrue(String director);
    
    // Find movies by language
    List<Movie> findByLanguageAndIsActiveTrue(String language);
    
    // Find currently showing movies
    @Query("SELECT m FROM Movie m WHERE m.isActive = true AND " +
           "(m.releaseDate IS NULL OR m.releaseDate <= :now) AND " +
           "(m.endDate IS NULL OR m.endDate >= :now)")
    List<Movie> findCurrentlyShowingMovies(@Param("now") LocalDateTime now);
    
    // Find coming soon movies
    @Query("SELECT m FROM Movie m WHERE m.isActive = true AND m.releaseDate > :now")
    List<Movie> findComingSoonMovies(@Param("now") LocalDateTime now);
    
    // Find movies by date range
    @Query("SELECT m FROM Movie m WHERE m.isActive = true AND " +
           "((m.releaseDate IS NULL OR m.releaseDate <= :endDate) AND " +
           "(m.endDate IS NULL OR m.endDate >= :startDate))")
    List<Movie> findMoviesByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    // Find movies by multiple genres
    @Query("SELECT m FROM Movie m WHERE m.isActive = true AND m.genre IN :genres")
    List<Movie> findByGenresIn(@Param("genres") List<Movie.Genre> genres);
    
    // Find movies by multiple ratings
    @Query("SELECT m FROM Movie m WHERE m.isActive = true AND m.rating IN :ratings")
    List<Movie> findByRatingsIn(@Param("ratings") List<Movie.Rating> ratings);
    
    // Search movies by multiple criteria
    @Query("SELECT m FROM Movie m WHERE m.isActive = true AND " +
           "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:genre IS NULL OR m.genre = :genre) AND " +
           "(:rating IS NULL OR m.rating = :rating) AND " +
           "(:language IS NULL OR LOWER(m.language) LIKE LOWER(CONCAT('%', :language, '%')))")
    List<Movie> searchMovies(@Param("title") String title,
                            @Param("genre") Movie.Genre genre,
                            @Param("rating") Movie.Rating rating,
                            @Param("language") String language);
    
    // Paginated search
    @Query("SELECT m FROM Movie m WHERE m.isActive = true AND " +
           "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:genre IS NULL OR m.genre = :genre) AND " +
           "(:rating IS NULL OR m.rating = :rating) AND " +
           "(:language IS NULL OR LOWER(m.language) LIKE LOWER(CONCAT('%', :language, '%')))")
    Page<Movie> searchMoviesPaginated(@Param("title") String title,
                                     @Param("genre") Movie.Genre genre,
                                     @Param("rating") Movie.Rating rating,
                                     @Param("language") String language,
                                     Pageable pageable);
    
    // Count movies by genre
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.genre = :genre AND m.isActive = true")
    Long countByGenre(@Param("genre") Movie.Genre genre);
    
    // Count movies by rating
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.rating = :rating AND m.isActive = true")
    Long countByRating(@Param("rating") Movie.Rating rating);
    
    // Find movies with no poster
    List<Movie> findByPosterUrlIsNullAndIsActiveTrue();
    
    
    // Find movies ending soon (within next 7 days)
    @Query("SELECT m FROM Movie m WHERE m.isActive = true AND m.endDate IS NOT NULL AND m.endDate BETWEEN :now AND :weekFromNow")
    List<Movie> findMoviesEndingSoon(@Param("now") LocalDateTime now, 
                                    @Param("weekFromNow") LocalDateTime weekFromNow);
    
    // Find movies by exact title
    Optional<Movie> findByTitleIgnoreCaseAndIsActiveTrue(String title);
    
    // Check if movie title exists (excluding current movie for updates)
    @Query("SELECT COUNT(m) > 0 FROM Movie m WHERE LOWER(m.title) = LOWER(:title) AND m.isActive = true AND (:id IS NULL OR m.id != :id)")
    boolean existsByTitleIgnoreCaseAndIsActiveTrue(@Param("title") String title, @Param("id") Long id);

    // Analytics methods
    Long countByIsActive(Boolean isActive);

    Long countByIsFeatured(Boolean isFeatured);

    @Query("SELECT DISTINCT m.genre FROM Movie m WHERE m.isActive = true")
    List<Movie.Genre> findDistinctGenres();

    @Query("SELECT DISTINCT m.language FROM Movie m WHERE m.isActive = true")
    List<String> findDistinctLanguages();
}

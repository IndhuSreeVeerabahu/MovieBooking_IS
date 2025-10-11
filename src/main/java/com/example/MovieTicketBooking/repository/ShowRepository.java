package com.example.MovieTicketBooking.repository;

import com.example.MovieTicketBooking.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long>, JpaSpecificationExecutor<Show> {

    // Find shows by movie
    List<Show> findByMovieIdAndIsActiveTrue(Long movieId);
    List<Show> findByMovieIdAndIsActiveTrueOrderByShowDateAscShowTimeAsc(Long movieId);

    // Find shows by theater
    List<Show> findByTheaterIdAndIsActiveTrue(Long theaterId);
    List<Show> findByTheaterIdAndIsActiveTrueOrderByShowDateAscShowTimeAsc(Long theaterId);

    // Find shows by screen
    List<Show> findByScreenIdAndIsActiveTrue(Long screenId);

    // Find shows by date range
    @Query("SELECT s FROM Show s WHERE s.isActive = true AND s.showDate BETWEEN :startDate AND :endDate")
    List<Show> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    List<Show> findByShowDateBetweenAndIsActiveTrueOrderByShowDateAscShowTimeAsc(LocalDate startDate, LocalDate endDate);

    // Find shows by movie and date
    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId AND s.isActive = true AND s.showDate = :showDate")
    List<Show> findByMovieAndDate(@Param("movieId") Long movieId, @Param("showDate") LocalDate showDate);

    // Find shows by movie, theater, and date
    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId AND s.theater.id = :theaterId AND s.isActive = true AND s.showDate = :showDate")
    List<Show> findByMovieTheaterAndDate(@Param("movieId") Long movieId, @Param("theaterId") Long theaterId, @Param("showDate") LocalDate showDate);

    // Find available shows (not sold out, not cancelled, not started)
    @Query("SELECT s FROM Show s WHERE s.isActive = true AND s.showStatus = 'ACTIVE' AND s.showDate >= :today")
    List<Show> findAvailableShows(@Param("today") LocalDate today);

    // Find shows by status
    List<Show> findByShowStatusAndIsActiveTrue(Show.ShowStatus showStatus);

    // Find upcoming shows
    @Query("SELECT s FROM Show s WHERE s.isActive = true AND s.showDate >= :today ORDER BY s.showDate ASC")
    List<Show> findUpcomingShows(@Param("today") LocalDate today);

    // Find shows by movie and theater
    List<Show> findByMovieIdAndTheaterIdAndIsActiveTrue(Long movieId, Long theaterId);
    List<Show> findByMovieIdAndTheaterIdAndIsActiveTrueOrderByShowDateAscShowTimeAsc(Long movieId, Long theaterId);

    // Find shows by city and date
    @Query("SELECT s FROM Show s WHERE s.theater.city = :city AND s.isActive = true AND s.showDate = :showDate")
    List<Show> findByCityAndDate(@Param("city") String city, @Param("showDate") LocalDate showDate);

    // Find shows by movie, city, and date
    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId AND s.theater.city = :city AND s.isActive = true AND s.showDate = :showDate")
    List<Show> findByMovieCityAndDate(@Param("movieId") Long movieId, @Param("city") String city, @Param("showDate") LocalDate showDate);

    // Find shows with pagination
    @Query("SELECT s FROM Show s WHERE s.isActive = true AND s.showDate >= :today")
    Page<Show> findUpcomingShowsPaginated(@Param("today") LocalDate today, Pageable pageable);

    // Find shows by multiple criteria
    @Query("SELECT s FROM Show s WHERE s.isActive = true AND " +
           "(:movieId IS NULL OR s.movie.id = :movieId) AND " +
           "(:theaterId IS NULL OR s.theater.id = :theaterId) AND " +
           "(:city IS NULL OR s.theater.city = :city) AND " +
           "(:showDate IS NULL OR s.showDate = :showDate) AND " +
           "s.showDate >= :today")
    List<Show> searchShows(@Param("movieId") Long movieId,
                           @Param("theaterId") Long theaterId,
                           @Param("city") String city,
                           @Param("showDate") LocalDate showDate,
                           @Param("today") LocalDate today);

    // Find shows ending soon (for cleanup)
    @Query("SELECT s FROM Show s WHERE s.isActive = true AND s.endTime < :now AND s.showStatus = 'ACTIVE'")
    List<Show> findShowsEndingSoon(@Param("now") LocalDateTime now);

    // Count shows by movie
    @Query("SELECT COUNT(s) FROM Show s WHERE s.movie.id = :movieId AND s.isActive = true")
    Long countByMovieId(@Param("movieId") Long movieId);

    // Count shows by theater
    @Query("SELECT COUNT(s) FROM Show s WHERE s.theater.id = :theaterId AND s.isActive = true")
    Long countByTheaterId(@Param("theaterId") Long theaterId);

    // Find shows with available seats
    @Query("SELECT s FROM Show s WHERE s.isActive = true AND s.showStatus = 'ACTIVE' AND s.showDate >= :today AND " +
           "s.id NOT IN (SELECT DISTINCT bs.booking.show.id FROM BookingSeat bs WHERE bs.booking.bookingStatus = 'CONFIRMED')")
    List<Show> findShowsWithAvailableSeats(@Param("today") LocalDate today);

    // Find conflicting shows on the same screen
    @Query("SELECT s FROM Show s WHERE s.screen.id = :screenId AND s.isActive = true AND " +
           "s.showDate = :showDate AND " +
           "((s.showTime <= :startTime AND s.endTime > :startTime) OR " +
           "(s.showTime < :endTime AND s.endTime >= :endTime) OR " +
           "(s.showTime >= :startTime AND s.endTime <= :endTime))")
    List<Show> findConflictingShows(@Param("screenId") Long screenId, 
                                   @Param("showDate") java.time.LocalDate showDate,
                                   @Param("startTime") java.time.LocalTime startTime,
                                   @Param("endTime") java.time.LocalTime endTime);

    // Find conflicting shows excluding current show
    @Query("SELECT s FROM Show s WHERE s.screen.id = :screenId AND s.isActive = true AND s.id != :excludeShowId AND " +
           "s.showDate = :showDate AND " +
           "((s.showTime <= :startTime AND s.endTime > :startTime) OR " +
           "(s.showTime < :endTime AND s.endTime >= :endTime) OR " +
           "(s.showTime >= :startTime AND s.endTime <= :endTime))")
    List<Show> findConflictingShowsExcluding(@Param("screenId") Long screenId, 
                                           @Param("showDate") java.time.LocalDate showDate,
                                           @Param("startTime") java.time.LocalTime startTime,
                                           @Param("endTime") java.time.LocalTime endTime,
                                           @Param("excludeShowId") Long excludeShowId);

    // Analytics methods
    @Query("SELECT DISTINCT s.movie.language FROM Show s WHERE s.isActive = true")
    List<String> findDistinctLanguages();
}

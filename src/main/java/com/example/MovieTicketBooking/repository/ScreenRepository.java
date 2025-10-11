package com.example.MovieTicketBooking.repository;

import com.example.MovieTicketBooking.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    /**
     * Find all screens by theater ID
     */
    List<Screen> findByTheaterIdAndIsActiveTrue(Long theaterId);

    /**
     * Find all screens by theater ID (including inactive)
     */
    List<Screen> findByTheaterId(Long theaterId);

    /**
     * Find screen by theater ID and screen name
     */
    Optional<Screen> findByTheaterIdAndScreenNameAndIsActiveTrue(Long theaterId, String screenName);

    /**
     * Find all active screens
     */
    List<Screen> findByIsActiveTrue();

    /**
     * Find all screens by theater ID ordered by screen name
     */
    @Query("SELECT s FROM Screen s WHERE s.theater.id = :theaterId AND s.isActive = true ORDER BY s.screenName ASC")
    List<Screen> findActiveScreensByTheaterOrderedByName(@Param("theaterId") Long theaterId);

    /**
     * Count screens by theater ID
     */
    long countByTheaterIdAndIsActiveTrue(Long theaterId);

    /**
     * Check if screen exists by theater ID and screen name
     */
    boolean existsByTheaterIdAndScreenNameAndIsActiveTrue(Long theaterId, String screenName);

    // Analytics methods
    @Query("SELECT DISTINCT s.screenType FROM Screen s WHERE s.isActive = true")
    List<Screen.ScreenType> findDistinctScreenTypes();
}
package com.example.MovieTicketBooking.repository;

import com.example.MovieTicketBooking.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long>, JpaSpecificationExecutor<Theater> {

    // Find active theaters
    List<Theater> findByIsActiveTrue();
    
    // Find active theaters with screens
    @Query("SELECT DISTINCT t FROM Theater t LEFT JOIN FETCH t.screens s WHERE t.isActive = true AND (s IS NULL OR s.isActive = true)")
    List<Theater> findByIsActiveTrueWithScreens();

    // Find theaters by city
    List<Theater> findByCityAndIsActiveTrue(String city);
    
    // Find theaters by city (case insensitive)
    List<Theater> findByCityIgnoreCaseAndIsActiveTrue(String city);

    // Find theaters by state
    List<Theater> findByStateAndIsActiveTrue(String state);

    // Find theaters by name (case insensitive)
    List<Theater> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

    // Find theaters by city and state
    List<Theater> findByCityAndStateAndIsActiveTrue(String city, String state);

    // Search theaters by multiple criteria
    @Query("SELECT t FROM Theater t WHERE t.isActive = true AND " +
           "(:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:city IS NULL OR LOWER(t.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:state IS NULL OR LOWER(t.state) LIKE LOWER(CONCAT('%', :state, '%')))")
    List<Theater> searchTheaters(@Param("name") String name,
                                 @Param("city") String city,
                                 @Param("state") String state);

    // Find theaters with specific amenities
    @Query("SELECT t FROM Theater t WHERE t.isActive = true AND LOWER(t.amenities) LIKE LOWER(CONCAT('%', :amenity, '%'))")
    List<Theater> findByAmenity(@Param("amenity") String amenity);

    // Find theaters by pincode
    List<Theater> findByPincodeAndIsActiveTrue(String pincode);

    // Check if theater name exists (excluding current theater for updates)
    @Query("SELECT COUNT(t) > 0 FROM Theater t WHERE LOWER(t.name) = LOWER(:name) AND t.isActive = true AND (:id IS NULL OR t.id != :id)")
    boolean existsByNameIgnoreCaseAndIsActiveTrue(@Param("name") String name, @Param("id") Long id);

    // Find theaters with minimum number of screens
    @Query("SELECT t FROM Theater t WHERE t.isActive = true AND t.totalScreens >= :minScreens")
    List<Theater> findByMinScreens(@Param("minScreens") Integer minScreens);

    // Analytics methods
    Long countByIsActive(Boolean isActive);

    @Query("SELECT DISTINCT t.city FROM Theater t WHERE t.isActive = true")
    List<String> findDistinctCities();

    @Query("SELECT DISTINCT t.state FROM Theater t WHERE t.isActive = true")
    List<String> findDistinctStates();
    
    // Check if theater has active shows
    @Query("SELECT COUNT(s) > 0 FROM Show s WHERE s.theater.id = :theaterId AND s.isActive = true")
    boolean existsByIdAndShowsIsActiveTrue(@Param("theaterId") Long theaterId);
    
    // Check if theater has active screens
    @Query("SELECT COUNT(sc) > 0 FROM Screen sc WHERE sc.theater.id = :theaterId AND sc.isActive = true")
    boolean existsByIdAndScreensIsActiveTrue(@Param("theaterId") Long theaterId);
}

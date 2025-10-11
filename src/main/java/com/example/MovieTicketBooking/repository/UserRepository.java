package com.example.MovieTicketBooking.repository;

import com.example.MovieTicketBooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email address
     * @param email the email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a user exists with the given email
     * @param email the email address
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find user by email and enabled status
     * @param email the email address
     * @param isEnabled the enabled status
     * @return Optional containing the user if found
     */
    Optional<User> findByEmailAndIsEnabled(String email, Boolean isEnabled);

    // Analytics methods
    Long countByIsEnabled(Boolean isEnabled);

    Long countByCreatedAtAfter(java.time.LocalDateTime dateTime);

    Long countByCreatedAtBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}

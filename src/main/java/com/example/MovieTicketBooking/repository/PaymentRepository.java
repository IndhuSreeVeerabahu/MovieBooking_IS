package com.example.MovieTicketBooking.repository;

import com.example.MovieTicketBooking.entity.Payment;
import com.example.MovieTicketBooking.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find payment by booking ID
     */
    Optional<Payment> findByBookingId(Long bookingId);
    
    /**
     * Find payment by Cashfree order ID
     */
    Optional<Payment> findByCashfreeOrderId(String cashfreeOrderId);
    
    /**
     * Find payment by Cashfree payment ID
     */
    Optional<Payment> findByCashfreePaymentId(String cashfreePaymentId);
    
    /**
     * Find payment by payment session ID
     */
    Optional<Payment> findByPaymentSessionId(String paymentSessionId);
    
    /**
     * Find all payments for a specific user
     */
    @Query("SELECT p FROM Payment p WHERE p.booking.user.id = :userId ORDER BY p.createdAt DESC")
    List<Payment> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find all payments by status
     */
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Find payments by booking ID and status
     */
    @Query("SELECT p FROM Payment p WHERE p.booking.id = :bookingId AND p.paymentStatus = :status")
    Optional<Payment> findByBookingIdAndPaymentStatus(@Param("bookingId") Long bookingId, 
                                                     @Param("status") PaymentStatus status);
    
    /**
     * Count payments by status
     */
    long countByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Find recent payments (last 30 days)
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :startDate ORDER BY p.createdAt DESC")
    List<Payment> findRecentPayments(@Param("startDate") java.time.LocalDateTime startDate);
    
    /**
     * Find all payments ordered by creation date (most recent first)
     */
    List<Payment> findAllByOrderByCreatedAtDesc();
}

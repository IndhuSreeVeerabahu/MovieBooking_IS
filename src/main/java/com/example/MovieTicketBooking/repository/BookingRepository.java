package com.example.MovieTicketBooking.repository;

import com.example.MovieTicketBooking.entity.Booking;
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
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    // Find bookings by user
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Find bookings by user with pagination
    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Find booking by reference
    Optional<Booking> findByBookingReference(String bookingReference);

    // Find booking by order ID
    Optional<Booking> findByOrderId(String orderId);

    // Find bookings by show
    List<Booking> findByShowIdAndBookingStatus(Long showId, Booking.BookingStatus bookingStatus);

    // Find confirmed bookings by show
    List<Booking> findByShowIdAndBookingStatusAndPaymentStatus(Long showId, 
                                                               Booking.BookingStatus bookingStatus, 
                                                               Booking.PaymentStatus paymentStatus);

    // Find bookings by status
    List<Booking> findByBookingStatusOrderByCreatedAtDesc(Booking.BookingStatus bookingStatus);

    // Find bookings by payment status
    List<Booking> findByPaymentStatusOrderByCreatedAtDesc(Booking.PaymentStatus paymentStatus);

    // Find expired bookings
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = 'PENDING' AND b.expiryTime < :now")
    List<Booking> findExpiredBookings(@Param("now") LocalDateTime now);

    // Find bookings by date range
    @Query("SELECT b FROM Booking b WHERE b.bookingDate BETWEEN :startDate AND :endDate")
    List<Booking> findByBookingDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find bookings by show date range
    @Query("SELECT b FROM Booking b WHERE b.show.showDate BETWEEN :startDate AND :endDate")
    List<Booking> findByShowDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find user's upcoming bookings
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.show.showDate > :now AND b.bookingStatus = 'CONFIRMED' ORDER BY b.show.showDate ASC")
    List<Booking> findUserUpcomingBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Find user's past bookings
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.show.showDate < :now ORDER BY b.show.showDate DESC")
    List<Booking> findUserPastBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Find cancellable bookings
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.bookingStatus = 'CONFIRMED' AND b.paymentStatus = 'COMPLETED' AND b.show.showDate > :cutoffTime")
    List<Booking> findCancellableBookings(@Param("userId") Long userId, @Param("cutoffTime") LocalDateTime cutoffTime);

    // Count bookings by user
    Long countByUserId(Long userId);

    // Count confirmed bookings by show
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.show.id = :showId AND b.bookingStatus = 'CONFIRMED' AND b.paymentStatus = 'COMPLETED'")
    Long countConfirmedBookingsByShow(@Param("showId") Long showId);

    // Find bookings by movie
    @Query("SELECT b FROM Booking b WHERE b.show.movie.id = :movieId ORDER BY b.createdAt DESC")
    List<Booking> findByMovieId(@Param("movieId") Long movieId);

    // Find bookings by theater
    @Query("SELECT b FROM Booking b WHERE b.show.theater.id = :theaterId ORDER BY b.createdAt DESC")
    List<Booking> findByTheaterId(@Param("theaterId") Long theaterId);

    // Find recent bookings
    @Query("SELECT b FROM Booking b WHERE b.bookingDate >= :since ORDER BY b.bookingDate DESC")
    List<Booking> findRecentBookings(@Param("since") LocalDateTime since);

    // Find bookings requiring cleanup (expired pending bookings)
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = 'PENDING' AND (b.expiryTime < :now OR b.createdAt < :cutoffTime)")
    List<Booking> findBookingsForCleanup(@Param("now") LocalDateTime now, @Param("cutoffTime") LocalDateTime cutoffTime);

    // Find bookings by payment method
    List<Booking> findByPaymentMethodOrderByCreatedAtDesc(String paymentMethod);

    // Find bookings with refunds
    @Query("SELECT b FROM Booking b WHERE b.paymentStatus IN ('REFUNDED', 'PARTIALLY_REFUNDED') ORDER BY b.updatedAt DESC")
    List<Booking> findRefundedBookings();

    // Check if booking reference exists
    boolean existsByBookingReference(String bookingReference);

    // Analytics methods
    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.bookingStatus = 'CONFIRMED' AND b.paymentStatus = 'COMPLETED'")
    java.math.BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.bookingStatus = 'CONFIRMED' AND b.paymentStatus = 'COMPLETED' AND YEAR(b.bookingDate) = :year AND MONTH(b.bookingDate) = :month")
    java.math.BigDecimal getRevenueForMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.bookingStatus = 'CONFIRMED' AND b.paymentStatus = 'COMPLETED' AND b.bookingDate >= :startDate")
    java.math.BigDecimal getRevenueForWeek(@Param("startDate") java.time.LocalDate startDate);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.bookingStatus = 'CONFIRMED' AND b.paymentStatus = 'COMPLETED' AND DATE(b.bookingDate) = :date")
    java.math.BigDecimal getRevenueForDate(@Param("date") java.time.LocalDate date);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.bookingStatus = 'CANCELLED'")
    java.math.BigDecimal getTotalRefunds();

    Long countByBookingStatus(Booking.BookingStatus bookingStatus);

    Long countByBookingDateBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    @Query("SELECT DISTINCT b.show.screen.theater.city FROM Booking b")
    List<String> findDistinctCities();

    @Query("SELECT b FROM Booking b ORDER BY b.createdAt DESC")
    List<Booking> findTop10ByOrderByCreatedAtDesc();
}

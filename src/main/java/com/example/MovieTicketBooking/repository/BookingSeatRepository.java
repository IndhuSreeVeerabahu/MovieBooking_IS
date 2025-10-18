package com.example.MovieTicketBooking.repository;

import com.example.MovieTicketBooking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    // Find booking seats by booking
    List<BookingSeat> findByBookingId(Long bookingId);

    // Find booking seats by show
    @Query("SELECT bs FROM BookingSeat bs WHERE bs.booking.show.id = :showId AND bs.seatStatus = 'BOOKED'")
    List<BookingSeat> findByShowIdAndBooked(@Param("showId") Long showId);

    // Find booked seat IDs by show (including PENDING bookings to prevent double booking)
    @Query("SELECT bs.seat.id FROM BookingSeat bs WHERE bs.booking.show.id = :showId AND bs.booking.bookingStatus IN ('CONFIRMED', 'PENDING') AND bs.seatStatus = 'BOOKED'")
    Set<Long> findBookedSeatIdsByShowId(@Param("showId") Long showId);

    // Find booking seats by seat
    List<BookingSeat> findBySeatIdAndSeatStatus(Long seatId, BookingSeat.SeatStatus seatStatus);

    // Find active booking seats by show
    @Query("SELECT bs FROM BookingSeat bs WHERE bs.booking.show.id = :showId AND bs.booking.bookingStatus IN ('CONFIRMED', 'PENDING') AND bs.seatStatus = 'BOOKED'")
    List<BookingSeat> findActiveBookingSeatsByShowId(@Param("showId") Long showId);

    // Count booked seats by show
    @Query("SELECT COUNT(bs) FROM BookingSeat bs WHERE bs.booking.show.id = :showId AND bs.booking.bookingStatus = 'CONFIRMED' AND bs.seatStatus = 'BOOKED'")
    Long countBookedSeatsByShowId(@Param("showId") Long showId);

    // Find booking seats by booking status
    @Query("SELECT bs FROM BookingSeat bs WHERE bs.booking.bookingStatus = :bookingStatus")
    List<BookingSeat> findByBookingStatus(@Param("bookingStatus") String bookingStatus);

    // Find booking seats by user
    @Query("SELECT bs FROM BookingSeat bs WHERE bs.booking.user.id = :userId ORDER BY bs.booking.createdAt DESC")
    List<BookingSeat> findByUserId(@Param("userId") Long userId);

    // Find booking seats by movie
    @Query("SELECT bs FROM BookingSeat bs WHERE bs.booking.show.movie.id = :movieId ORDER BY bs.booking.createdAt DESC")
    List<BookingSeat> findByMovieId(@Param("movieId") Long movieId);

    // Find booking seats by theater
    @Query("SELECT bs FROM BookingSeat bs WHERE bs.booking.show.theater.id = :theaterId ORDER BY bs.booking.createdAt DESC")
    List<BookingSeat> findByTheaterId(@Param("theaterId") Long theaterId);
}

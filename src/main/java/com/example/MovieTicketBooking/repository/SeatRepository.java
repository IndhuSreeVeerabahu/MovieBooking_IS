package com.example.MovieTicketBooking.repository;

import com.example.MovieTicketBooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    // Find seats by screen
    List<Seat> findByScreenIdAndIsActiveTrue(Long screenId);

    // Find seats by screen and row
    List<Seat> findByScreenIdAndRowNumberAndIsActiveTrue(Long screenId, Integer rowNumber);

    // Find seats by type
    List<Seat> findBySeatTypeAndIsActiveTrue(Seat.SeatType seatType);

    // Find seats by screen and type
    List<Seat> findByScreenIdAndSeatTypeAndIsActiveTrue(Long screenId, Seat.SeatType seatType);

    // Find specific seat
    @Query("SELECT s FROM Seat s WHERE s.screen.id = :screenId AND s.rowNumber = :rowNumber AND s.seatNumber = :seatNumber AND s.isActive = true")
    Seat findByScreenAndPosition(@Param("screenId") Long screenId, @Param("rowNumber") Integer rowNumber, @Param("seatNumber") Integer seatNumber);


    // Count seats by screen
    Long countByScreenIdAndIsActiveTrue(Long screenId);

    // Count seats by screen and type
    Long countByScreenIdAndSeatTypeAndIsActiveTrue(Long screenId, Seat.SeatType seatType);

    // Find premium seats by screen
    @Query("SELECT s FROM Seat s WHERE s.screen.id = :screenId AND s.seatType IN ('PREMIUM', 'VIP') AND s.isActive = true")
    List<Seat> findPremiumSeatsByScreen(@Param("screenId") Long screenId);

    // Find seats by screen ID (for SeatLayoutService)
    List<Seat> findByScreenId(Long screenId);
}

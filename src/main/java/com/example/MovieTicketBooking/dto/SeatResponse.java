package com.example.MovieTicketBooking.dto;

import com.example.MovieTicketBooking.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {

    private Long id;
    private Integer rowNumber;
    private Integer seatNumber;
    private String seatIdentifier;
    private Seat.SeatType seatType;
    private BigDecimal price;
    private Boolean isAvailable;
    private Boolean isBooked;
    private Boolean isLocked;

    // Helper methods
    public String getRowLetter() {
        return String.valueOf((char) ('A' + rowNumber - 1));
    }

    public boolean isPremiumSeat() {
        return seatType == Seat.SeatType.PREMIUM || seatType == Seat.SeatType.VIP;
    }
}

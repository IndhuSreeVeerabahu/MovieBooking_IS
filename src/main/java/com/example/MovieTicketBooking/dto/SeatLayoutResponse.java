package com.example.MovieTicketBooking.dto;

import com.example.MovieTicketBooking.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatLayoutResponse {

    private Long screenId;
    private String screenName;
    private String layoutName;
    private Integer totalRows;
    private Integer totalSeatsPerRow;
    private Integer totalSeats;
    private List<SeatRowInfo> seatRows;
    private Map<String, Integer> seatTypeCounts;
    private List<String> availableSeats;
    private List<String> bookedSeats;
    private List<String> lockedSeats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatRowInfo {
        private Integer rowNumber;
        private String rowLetter;
        private Integer seatsPerRow;
        private String seatType;
        private Boolean hasAisle;
        private Integer aislePosition;
        private List<SeatInfo> seats;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInfo {
        private Long seatId;
        private String seatIdentifier;
        private String seatType;
        private String status; // AVAILABLE, BOOKED, LOCKED, DISABLED
        private Boolean isActive;
        private Integer rowNumber;
        private Integer seatNumber;
    }
}
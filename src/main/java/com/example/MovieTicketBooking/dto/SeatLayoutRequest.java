package com.example.MovieTicketBooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatLayoutRequest {

    @NotNull(message = "Screen ID is required")
    private Long screenId;

    @NotBlank(message = "Layout name is required")
    private String layoutName;

    @Min(value = 1, message = "Total rows must be at least 1")
    private Integer totalRows;

    @Min(value = 1, message = "Total seats per row must be at least 1")
    private Integer totalSeatsPerRow;

    private List<SeatRowConfig> seatRows;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatRowConfig {
        @Min(value = 1, message = "Row number must be at least 1")
        private Integer rowNumber;

        @Min(value = 1, message = "Seats per row must be at least 1")
        private Integer seatsPerRow;

        private String seatType; // STANDARD, PREMIUM, VIP

        private Boolean hasAisle; // Whether this row has an aisle

        private Integer aislePosition; // Position of aisle (0-based)
    }
}

package com.example.MovieTicketBooking.dto;

import com.example.MovieTicketBooking.entity.Show;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowResponse {

    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long theaterId;
    private String theaterName;
    private Long screenId;
    private String screenName;
    private LocalDate showDate;
    private LocalTime showTime;
    private LocalTime endTime;
    private BigDecimal basePrice;
    private BigDecimal premiumPrice;
    private BigDecimal vipPrice;
    private Show.ShowStatus showStatus;
    private Boolean isActive;
    private Boolean isBookingOpen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper methods
    public LocalDate getShowDateOnly() {
        return showDate;
    }

    public String getFormattedShowTime() {
        return showTime.toString();
    }

    public String getFormattedEndTime() {
        return endTime.toString();
    }
}

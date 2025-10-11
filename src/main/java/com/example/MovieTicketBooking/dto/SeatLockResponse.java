package com.example.MovieTicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatLockResponse {

    private String lockId;
    private Long showId;
    private List<Long> seatIds;
    private BigDecimal totalAmount;
    private LocalDateTime expiresAt;

    // Helper methods
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public long getMinutesUntilExpiry() {
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
    }
}

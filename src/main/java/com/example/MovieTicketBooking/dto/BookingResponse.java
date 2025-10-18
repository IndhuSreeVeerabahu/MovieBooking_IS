package com.example.MovieTicketBooking.dto;

import com.example.MovieTicketBooking.entity.Booking;
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
public class BookingResponse {

    private Long id;
    private String bookingReference;
    private Long userId;
    private String userName;
    private Long showId;
    private String movieTitle;
    private String theaterName;
    private LocalDateTime showDateTime;
    private BigDecimal totalAmount;
    private BigDecimal bookingFee;
    private BigDecimal taxAmount;
    private Booking.BookingStatus bookingStatus;
    private Booking.PaymentStatus paymentStatus;
    private String paymentMethod;
    private LocalDateTime bookingDate;
    private LocalDateTime expiryTime;
    private List<String> seatNumbers;

    // Helper methods
    public boolean isConfirmed() {
        return bookingStatus == Booking.BookingStatus.CONFIRMED && 
               paymentStatus == Booking.PaymentStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return bookingStatus == Booking.BookingStatus.CONFIRMED;
    }

    public boolean isExpired() {
        return bookingStatus == Booking.BookingStatus.EXPIRED || 
               (expiryTime != null && expiryTime.isBefore(LocalDateTime.now()));
    }

    public BigDecimal getTotalWithFees() {
        BigDecimal total = totalAmount;
        if (bookingFee != null) total = total.add(bookingFee);
        if (taxAmount != null) total = total.add(taxAmount);
        return total;
    }
}

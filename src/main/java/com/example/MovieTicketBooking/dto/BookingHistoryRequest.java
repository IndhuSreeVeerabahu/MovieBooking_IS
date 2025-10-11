package com.example.MovieTicketBooking.dto;

import com.example.MovieTicketBooking.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistoryRequest {

    private Long userId;
    private List<Booking.BookingStatus> bookingStatuses;
    private List<Booking.PaymentStatus> paymentStatuses;
    private LocalDate bookingDateFrom;
    private LocalDate bookingDateTo;
    private LocalDate showDateFrom;
    private LocalDate showDateTo;
    private String movieTitle;
    private String theaterName;
    private String city;
    private String sortBy; // bookingDate, showDate, totalAmount, movieTitle
    private String sortOrder; // ASC, DESC
    private Integer page;
    private Integer size;
}

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
public class BookingHistoryResponse {

    private List<BookingHistoryItem> bookings;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private BookingSummary summary;
    private List<String> availableStatuses;
    private List<String> availableCities;
    private BookingHistoryFilters appliedFilters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingHistoryItem {
        private Long bookingId;
        private String bookingReference;
        private String movieTitle;
        private String theaterName;
        private String screenName;
        private String city;
        private LocalDateTime showTime;
        private LocalDateTime bookingDate;
        private String bookingStatus;
        private String paymentStatus;
        private BigDecimal totalAmount;
        private BigDecimal bookingFee;
        private BigDecimal taxAmount;
        private String paymentMethod;
        private List<String> seatNumbers;
        private String cancellationReason;
        private LocalDateTime cancelledAt;
        private boolean canBeCancelled;
        private boolean canBeRefunded;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingSummary {
        private long totalBookings;
        private BigDecimal totalAmount;
        private BigDecimal totalRefunded;
        private long activeBookings;
        private long cancelledBookings;
        private long completedBookings;
        private BigDecimal averageBookingValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingHistoryFilters {
        private List<String> bookingStatuses;
        private List<String> paymentStatuses;
        private String bookingDateFrom;
        private String bookingDateTo;
        private String showDateFrom;
        private String showDateTo;
        private String movieTitle;
        private String theaterName;
        private String city;
        private String sortBy;
        private String sortOrder;
    }
}

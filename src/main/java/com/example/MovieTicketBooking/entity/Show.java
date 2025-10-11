package com.example.MovieTicketBooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "shows")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;

    @Column(name = "show_time", nullable = false)
    private LocalTime showTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "premium_price", precision = 10, scale = 2)
    private BigDecimal premiumPrice;

    @Column(name = "vip_price", precision = 10, scale = 2)
    private BigDecimal vipPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "show_status", nullable = false)
    @Builder.Default
    private ShowStatus showStatus = ShowStatus.ACTIVE;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    // Enums
    public enum ShowStatus {
        ACTIVE("Active"),
        CANCELLED("Cancelled"),
        COMPLETED("Completed"),
        SOLD_OUT("Sold Out");

        private final String displayName;

        ShowStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Helper methods
    public LocalDateTime getShowDateTime() {
        return showDate.atTime(showTime);
    }

    public LocalDateTime getEndDateTime() {
        return showDate.atTime(endTime);
    }

    public boolean isShowTimePassed() {
        return getShowDateTime().isBefore(LocalDateTime.now());
    }

    public boolean isShowCompleted() {
        return getEndDateTime().isBefore(LocalDateTime.now());
    }

    public boolean isBookingOpen() {
        return isActive && 
               showStatus == ShowStatus.ACTIVE && 
               !isShowTimePassed() &&
               getShowDateTime().isAfter(LocalDateTime.now().plusMinutes(30)); // Allow booking until 30 minutes before show
    }

    public BigDecimal getPriceForSeatType(Seat.SeatType seatType) {
        switch (seatType) {
            case PREMIUM:
                return premiumPrice != null ? premiumPrice : basePrice;
            case VIP:
                return vipPrice != null ? vipPrice : basePrice;
            default:
                return basePrice;
        }
    }
}

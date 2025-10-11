package com.example.MovieTicketBooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_reference", unique = true, nullable = false, length = 20)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "booking_fee", precision = 10, scale = 2)
    private BigDecimal bookingFee;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "order_id", length = 100)
    private String orderId;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingSeat> bookingSeats;

    // Enums
    public enum BookingStatus {
        PENDING("Pending"),
        CONFIRMED("Confirmed"),
        CANCELLED("Cancelled"),
        EXPIRED("Expired"),
        REFUNDED("Refunded");

        private final String displayName;

        BookingStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PaymentStatus {
        PENDING("Pending"),
        COMPLETED("Completed"),
        FAILED("Failed"),
        REFUNDED("Refunded"),
        PARTIALLY_REFUNDED("Partially Refunded");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Helper methods
    public boolean isConfirmed() {
        return bookingStatus == BookingStatus.CONFIRMED && paymentStatus == PaymentStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return bookingStatus == BookingStatus.CANCELLED;
    }

    public boolean isExpired() {
        return bookingStatus == BookingStatus.EXPIRED || 
               (expiryTime != null && expiryTime.isBefore(LocalDateTime.now()));
    }

    public boolean canBeCancelled() {
        return isConfirmed() && 
               show.getShowDateTime().isAfter(LocalDateTime.now().plusHours(2)); // Can cancel until 2 hours before show
    }

    public boolean canBeRefunded() {
        return isCancelled() && 
               show.getShowDateTime().isAfter(LocalDateTime.now()); // Can refund if show hasn't happened yet
    }

    public BigDecimal getTotalWithFees() {
        BigDecimal total = totalAmount;
        if (bookingFee != null) total = total.add(bookingFee);
        if (taxAmount != null) total = total.add(taxAmount);
        return total;
    }

    @PrePersist
    public void generateBookingReference() {
        if (bookingReference == null) {
            this.bookingReference = "BK" + System.currentTimeMillis();
        }
    }
}

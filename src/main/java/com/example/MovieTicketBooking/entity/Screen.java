package com.example.MovieTicketBooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "screens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(name = "screen_number", nullable = false)
    private Integer screenNumber;

    @Column(name = "screen_name", length = 100)
    private String screenName;

    @Column(name = "total_rows", nullable = false)
    private Integer totalRows;

    @Column(name = "total_seats_per_row", nullable = false)
    private Integer totalSeatsPerRow;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "screen_type", nullable = false)
    private ScreenType screenType;

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
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Show> shows;

    // Enums
    public enum ScreenType {
        STANDARD("Standard"),
        PREMIUM("Premium"),
        IMAX("IMAX"),
        DOLBY_ATMOS("Dolby Atmos"),
        VIP("VIP");

        private final String displayName;

        ScreenType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Helper methods
    public String getScreenDisplayName() {
        return screenName != null ? screenName : ("Screen " + screenNumber);
    }

    public void calculateTotalSeats() {
        this.totalSeats = this.totalRows * this.totalSeatsPerRow;
    }

    @PrePersist
    @PreUpdate
    public void calculateSeats() {
        calculateTotalSeats();
    }
}

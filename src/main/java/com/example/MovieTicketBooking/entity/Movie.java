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
@Table(name = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private Genre genre;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating", nullable = false)
    private Rating rating;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;


    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "director", length = 255)
    private String director;

    @Column(name = "cast", columnDefinition = "TEXT")
    private String cast;

    @Column(name = "language", length = 50)
    private String language;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Enums for Movie properties
    public enum Genre {
        ACTION("Action"),
        COMEDY("Comedy"),
        DRAMA("Drama"),
        HORROR("Horror"),
        ROMANCE("Romance"),
        THRILLER("Thriller"),
        SCI_FI("Sci-Fi"),
        FANTASY("Fantasy"),
        ADVENTURE("Adventure"),
        ANIMATION("Animation"),
        DOCUMENTARY("Documentary"),
        CRIME("Crime"),
        MYSTERY("Mystery"),
        FAMILY("Family"),
        MUSICAL("Musical"),
        WESTERN("Western"),
        WAR("War"),
        BIOGRAPHY("Biography"),
        HISTORY("History"),
        SPORT("Sport");

        private final String displayName;

        Genre(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Rating {
        G("G - General Audiences"),
        PG("PG - Parental Guidance"),
        PG_13("PG-13 - Parents Strongly Cautioned"),
        R("R - Restricted"),
        NC_17("NC-17 - No One 17 and Under Admitted");

        private final String displayName;

        Rating(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Helper methods
    public String getFormattedDuration() {
        if (durationMinutes == null) return "N/A";
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        }
        return String.format("%dm", minutes);
    }

    public boolean isCurrentlyShowing() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               (releaseDate == null || releaseDate.isBefore(now) || releaseDate.isEqual(now)) &&
               (endDate == null || endDate.isAfter(now));
    }

    public boolean isComingSoon() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && releaseDate != null && releaseDate.isAfter(now);
    }

    public boolean isEnded() {
        LocalDateTime now = LocalDateTime.now();
        return endDate != null && endDate.isBefore(now);
    }
}

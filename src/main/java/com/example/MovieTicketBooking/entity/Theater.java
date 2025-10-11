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
@Table(name = "theaters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pincode", length = 10)
    private String pincode;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "total_screens", nullable = false)
    @Builder.Default
    private Integer totalScreens = 1;

    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities; // JSON string of amenities

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
    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Screen> screens;

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Show> shows;

    // Helper methods
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (this.address != null) address.append(this.address);
        if (this.city != null) {
            if (address.length() > 0) address.append(", ");
            address.append(this.city);
        }
        if (this.state != null) {
            if (address.length() > 0) address.append(", ");
            address.append(this.state);
        }
        if (this.pincode != null) {
            if (address.length() > 0) address.append(" - ");
            address.append(this.pincode);
        }
        return address.toString();
    }

    public boolean hasAmenity(String amenity) {
        return amenities != null && amenities.toLowerCase().contains(amenity.toLowerCase());
    }
}

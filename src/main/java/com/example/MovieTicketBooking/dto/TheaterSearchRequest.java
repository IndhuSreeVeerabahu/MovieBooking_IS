package com.example.MovieTicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheaterSearchRequest {

    private String name;
    private String city;
    private String state;
    private String pincode;
    private List<String> amenities;
    private Boolean isActive;
    private String sortBy; // name, city, totalScreens
    private String sortOrder; // ASC, DESC
    private Integer page;
    private Integer size;
    private Double latitude; // For location-based search
    private Double longitude; // For location-based search
    private Double radius; // Search radius in kilometers
}

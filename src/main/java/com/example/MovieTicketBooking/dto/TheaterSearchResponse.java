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
public class TheaterSearchResponse {

    private List<TheaterResponse> theaters;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<String> availableCities;
    private List<String> availableStates;
    private List<String> availableAmenities;
    private TheaterSearchFilters appliedFilters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TheaterSearchFilters {
        private String name;
        private String city;
        private String state;
        private String pincode;
        private List<String> amenities;
        private String sortBy;
        private String sortOrder;
    }
}

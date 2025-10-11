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
public class MovieSearchResponse {

    private List<MovieResponse> movies;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<String> availableGenres;
    private List<String> availableLanguages;
    private List<String> availableCities;
    private SearchFilters appliedFilters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchFilters {
        private String title;
        private List<String> genres;
        private List<String> ratings;
        private List<String> languages;
        private String director;
        private String city;
        private String sortBy;
        private String sortOrder;
    }
}

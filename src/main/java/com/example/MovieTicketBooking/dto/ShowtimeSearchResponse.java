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
public class ShowtimeSearchResponse {

    private List<ShowtimeGroup> showtimeGroups;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<String> availableScreenTypes;
    private List<String> availableLanguages;
    private List<String> availableShowTimes;
    private ShowtimeSearchFilters appliedFilters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowtimeGroup {
        private String theaterName;
        private String theaterAddress;
        private String theaterCity;
        private Long theaterId;
        private List<ScreenShowtimes> screenShowtimes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScreenShowtimes {
        private Long screenId;
        private String screenName;
        private String screenType;
        private List<ShowResponse> shows;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowtimeSearchFilters {
        private Long movieId;
        private Long theaterId;
        private String showDate;
        private List<String> screenTypes;
        private List<String> languages;
        private String sortBy;
        private String sortOrder;
    }
}

package com.example.MovieTicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private DashboardOverview overview;
    private RevenueAnalytics revenue;
    private UserAnalytics users;
    private BookingAnalytics bookings;
    private MovieAnalytics movies;
    private TheaterAnalytics theaters;
    private List<RecentActivity> recentActivities;
    private List<ChartData> revenueChart;
    private List<ChartData> bookingChart;
    private List<ChartData> userChart;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardOverview {
        private long totalUsers;
        private long totalBookings;
        private long totalMovies;
        private long totalTheaters;
        private BigDecimal totalRevenue;
        private BigDecimal monthlyRevenue;
        private BigDecimal dailyRevenue;
        private long activeBookings;
        private long pendingBookings;
        private long cancelledBookings;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueAnalytics {
        private BigDecimal totalRevenue;
        private BigDecimal monthlyRevenue;
        private BigDecimal weeklyRevenue;
        private BigDecimal dailyRevenue;
        private BigDecimal averageBookingValue;
        private BigDecimal totalRefunds;
        private BigDecimal netRevenue;
        private List<RevenueByMonth> revenueByMonth;
        private List<RevenueByTheater> revenueByTheater;
        private List<RevenueByMovie> revenueByMovie;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAnalytics {
        private long totalUsers;
        private long activeUsers;
        private long newUsersThisMonth;
        private long newUsersThisWeek;
        private long newUsersToday;
        private double averageBookingsPerUser;
        private List<UserRegistrationTrend> registrationTrend;
        private List<TopUsers> topUsers;
        private Map<String, Long> usersByCity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingAnalytics {
        private long totalBookings;
        private long confirmedBookings;
        private long pendingBookings;
        private long cancelledBookings;
        private long completedBookings;
        private double bookingSuccessRate;
        private double cancellationRate;
        private List<BookingTrend> bookingTrend;
        private List<BookingByTimeSlot> bookingByTimeSlot;
        private List<BookingByDay> bookingByDay;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovieAnalytics {
        private long totalMovies;
        private long activeMovies;
        private long featuredMovies;
        private List<TopMovies> topMovies;
        private List<MoviePerformance> moviePerformance;
        private Map<String, Long> moviesByGenre;
        private Map<String, Long> moviesByLanguage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TheaterAnalytics {
        private long totalTheaters;
        private long activeTheaters;
        private long totalScreens;
        private List<TopTheaters> topTheaters;
        private Map<String, Long> theatersByCity;
        private Map<String, Long> theatersByState;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String type; // BOOKING, USER_REGISTRATION, MOVIE_ADDED, etc.
        private String description;
        private LocalDateTime timestamp;
        private String userEmail;
        private String details;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartData {
        private String label;
        private BigDecimal value;
        private String date;
    }

    // Nested classes for detailed analytics
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueByMonth {
        private String month;
        private BigDecimal revenue;
        private long bookingCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueByTheater {
        private String theaterName;
        private String city;
        private BigDecimal revenue;
        private long bookingCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueByMovie {
        private String movieTitle;
        private BigDecimal revenue;
        private long bookingCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRegistrationTrend {
        private String date;
        private long newUsers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopUsers {
        private String userEmail;
        private String userName;
        private long totalBookings;
        private BigDecimal totalSpent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingTrend {
        private String date;
        private long bookingCount;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingByTimeSlot {
        private String timeSlot;
        private long bookingCount;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingByDay {
        private String dayOfWeek;
        private long bookingCount;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopMovies {
        private String movieTitle;
        private long bookingCount;
        private BigDecimal revenue;
        private double rating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoviePerformance {
        private String movieTitle;
        private LocalDate releaseDate;
        private long totalBookings;
        private BigDecimal totalRevenue;
        private double occupancyRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopTheaters {
        private String theaterName;
        private String city;
        private long totalBookings;
        private BigDecimal totalRevenue;
        private long totalScreens;
    }
}

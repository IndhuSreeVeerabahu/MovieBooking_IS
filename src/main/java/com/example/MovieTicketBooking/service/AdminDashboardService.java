package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.AdminDashboardResponse;
import com.example.MovieTicketBooking.entity.Booking;
import com.example.MovieTicketBooking.entity.Movie;
import com.example.MovieTicketBooking.entity.Theater;
import com.example.MovieTicketBooking.entity.User;
import com.example.MovieTicketBooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;

    /**
     * Get comprehensive admin dashboard data
     */
    public AdminDashboardResponse getDashboardData() {
        log.info("Generating admin dashboard data");

        return AdminDashboardResponse.builder()
                .overview(generateOverview())
                .revenue(generateRevenueAnalytics())
                .users(generateUserAnalytics())
                .bookings(generateBookingAnalytics())
                .movies(generateMovieAnalytics())
                .theaters(generateTheaterAnalytics())
                .recentActivities(generateRecentActivities())
                .revenueChart(generateRevenueChart())
                .bookingChart(generateBookingChart())
                .userChart(generateUserChart())
                .build();
    }

    /**
     * Generate dashboard overview
     */
    private AdminDashboardResponse.DashboardOverview generateOverview() {
        long totalUsers = userRepository.count();
        long totalBookings = bookingRepository.count();
        long totalMovies = movieRepository.count();
        long totalTheaters = theaterRepository.count();

        BigDecimal totalRevenue = bookingRepository.getTotalRevenue();
        BigDecimal monthlyRevenue = bookingRepository.getRevenueForMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        BigDecimal dailyRevenue = bookingRepository.getRevenueForDate(LocalDate.now());

        long activeBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.CONFIRMED);
        long pendingBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.PENDING);
        long cancelledBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.CANCELLED);

        return AdminDashboardResponse.DashboardOverview.builder()
                .totalUsers(totalUsers)
                .totalBookings(totalBookings)
                .totalMovies(totalMovies)
                .totalTheaters(totalTheaters)
                .totalRevenue(totalRevenue)
                .monthlyRevenue(monthlyRevenue)
                .dailyRevenue(dailyRevenue)
                .activeBookings(activeBookings)
                .pendingBookings(pendingBookings)
                .cancelledBookings(cancelledBookings)
                .build();
    }

    /**
     * Generate revenue analytics
     */
    private AdminDashboardResponse.RevenueAnalytics generateRevenueAnalytics() {
        BigDecimal totalRevenue = bookingRepository.getTotalRevenue();
        BigDecimal monthlyRevenue = bookingRepository.getRevenueForMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        BigDecimal weeklyRevenue = bookingRepository.getRevenueForWeek(LocalDate.now());
        BigDecimal dailyRevenue = bookingRepository.getRevenueForDate(LocalDate.now());

        long totalBookings = bookingRepository.count();
        BigDecimal averageBookingValue = totalBookings > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;

        BigDecimal totalRefunds = bookingRepository.getTotalRefunds();
        BigDecimal netRevenue = totalRevenue.subtract(totalRefunds);

        return AdminDashboardResponse.RevenueAnalytics.builder()
                .totalRevenue(totalRevenue)
                .monthlyRevenue(monthlyRevenue)
                .weeklyRevenue(weeklyRevenue)
                .dailyRevenue(dailyRevenue)
                .averageBookingValue(averageBookingValue)
                .totalRefunds(totalRefunds)
                .netRevenue(netRevenue)
                .revenueByMonth(generateRevenueByMonth())
                .revenueByTheater(generateRevenueByTheater())
                .revenueByMovie(generateRevenueByMovie())
                .build();
    }

    /**
     * Generate user analytics
     */
    private AdminDashboardResponse.UserAnalytics generateUserAnalytics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsEnabled(true);
        
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate startOfWeek = LocalDate.now().minusDays(7);
        
        long newUsersThisMonth = userRepository.countByCreatedAtAfter(startOfMonth.atStartOfDay());
        long newUsersThisWeek = userRepository.countByCreatedAtAfter(startOfWeek.atStartOfDay());
        long newUsersToday = userRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay());

        long totalBookings = bookingRepository.count();
        double averageBookingsPerUser = totalUsers > 0 ? (double) totalBookings / totalUsers : 0.0;

        return AdminDashboardResponse.UserAnalytics.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .newUsersThisWeek(newUsersThisWeek)
                .newUsersToday(newUsersToday)
                .averageBookingsPerUser(averageBookingsPerUser)
                .registrationTrend(generateUserRegistrationTrend())
                .topUsers(generateTopUsers())
                .usersByCity(generateUsersByCity())
                .build();
    }

    /**
     * Generate booking analytics
     */
    private AdminDashboardResponse.BookingAnalytics generateBookingAnalytics() {
        long totalBookings = bookingRepository.count();
        long confirmedBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.CONFIRMED);
        long pendingBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.PENDING);
        long cancelledBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.CANCELLED);
        long completedBookings = bookingRepository.countByBookingStatus(Booking.BookingStatus.CONFIRMED);

        double bookingSuccessRate = totalBookings > 0 ? 
                (double) confirmedBookings / totalBookings * 100 : 0.0;
        double cancellationRate = totalBookings > 0 ? 
                (double) cancelledBookings / totalBookings * 100 : 0.0;

        return AdminDashboardResponse.BookingAnalytics.builder()
                .totalBookings(totalBookings)
                .confirmedBookings(confirmedBookings)
                .pendingBookings(pendingBookings)
                .cancelledBookings(cancelledBookings)
                .completedBookings(completedBookings)
                .bookingSuccessRate(bookingSuccessRate)
                .cancellationRate(cancellationRate)
                .bookingTrend(generateBookingTrend())
                .bookingByTimeSlot(generateBookingByTimeSlot())
                .bookingByDay(generateBookingByDay())
                .build();
    }

    /**
     * Generate movie analytics
     */
    private AdminDashboardResponse.MovieAnalytics generateMovieAnalytics() {
        long totalMovies = movieRepository.count();
        long activeMovies = movieRepository.countByIsActive(true);
        long featuredMovies = movieRepository.countByIsFeatured(true);

        return AdminDashboardResponse.MovieAnalytics.builder()
                .totalMovies(totalMovies)
                .activeMovies(activeMovies)
                .featuredMovies(featuredMovies)
                .topMovies(generateTopMovies())
                .moviePerformance(generateMoviePerformance())
                .moviesByGenre(generateMoviesByGenre())
                .moviesByLanguage(generateMoviesByLanguage())
                .build();
    }

    /**
     * Generate theater analytics
     */
    private AdminDashboardResponse.TheaterAnalytics generateTheaterAnalytics() {
        long totalTheaters = theaterRepository.count();
        long activeTheaters = theaterRepository.countByIsActive(true);
        long totalScreens = screenRepository.count();

        return AdminDashboardResponse.TheaterAnalytics.builder()
                .totalTheaters(totalTheaters)
                .activeTheaters(activeTheaters)
                .totalScreens(totalScreens)
                .topTheaters(generateTopTheaters())
                .theatersByCity(generateTheatersByCity())
                .theatersByState(generateTheatersByState())
                .build();
    }

    /**
     * Generate recent activities
     */
    private List<AdminDashboardResponse.RecentActivity> generateRecentActivities() {
        // Get recent bookings
        List<Booking> recentBookings = bookingRepository.findTop10ByOrderByCreatedAtDesc();
        
        return recentBookings.stream()
                .map(booking -> AdminDashboardResponse.RecentActivity.builder()
                        .type("BOOKING")
                        .description("New booking created")
                        .timestamp(booking.getCreatedAt())
                        .userEmail(booking.getUser().getEmail())
                        .details(String.format("Movie: %s, Theater: %s", 
                                booking.getShow().getMovie().getTitle(),
                                booking.getShow().getScreen().getTheater().getName()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Generate revenue chart data
     */
    private List<AdminDashboardResponse.ChartData> generateRevenueChart() {
        // Generate last 12 months revenue data
        List<AdminDashboardResponse.ChartData> chartData = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        
        for (int i = 11; i >= 0; i--) {
            LocalDate monthDate = currentDate.minusMonths(i);
            BigDecimal revenue = bookingRepository.getRevenueForMonth(monthDate.getYear(), monthDate.getMonthValue());
            
            chartData.add(AdminDashboardResponse.ChartData.builder()
                    .label(monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy")))
                    .value(revenue)
                    .date(monthDate.toString())
                    .build());
        }
        
        return chartData;
    }

    /**
     * Generate booking chart data
     */
    private List<AdminDashboardResponse.ChartData> generateBookingChart() {
        // Generate last 30 days booking data
        List<AdminDashboardResponse.ChartData> chartData = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        
        for (int i = 29; i >= 0; i--) {
            LocalDate dayDate = currentDate.minusDays(i);
            long bookingCount = bookingRepository.countByBookingDateBetween(
                    dayDate.atStartOfDay(), 
                    dayDate.atTime(23, 59, 59));
            
            chartData.add(AdminDashboardResponse.ChartData.builder()
                    .label(dayDate.format(DateTimeFormatter.ofPattern("MMM dd")))
                    .value(BigDecimal.valueOf(bookingCount))
                    .date(dayDate.toString())
                    .build());
        }
        
        return chartData;
    }

    /**
     * Generate user chart data
     */
    private List<AdminDashboardResponse.ChartData> generateUserChart() {
        // Generate last 30 days user registration data
        List<AdminDashboardResponse.ChartData> chartData = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        
        for (int i = 29; i >= 0; i--) {
            LocalDate dayDate = currentDate.minusDays(i);
            long userCount = userRepository.countByCreatedAtBetween(
                    dayDate.atStartOfDay(), 
                    dayDate.atTime(23, 59, 59));
            
            chartData.add(AdminDashboardResponse.ChartData.builder()
                    .label(dayDate.format(DateTimeFormatter.ofPattern("MMM dd")))
                    .value(BigDecimal.valueOf(userCount))
                    .date(dayDate.toString())
                    .build());
        }
        
        return chartData;
    }

    // Helper methods for generating detailed analytics
    private List<AdminDashboardResponse.RevenueByMonth> generateRevenueByMonth() {
        // Implementation for revenue by month
        return new ArrayList<>();
    }

    private List<AdminDashboardResponse.RevenueByTheater> generateRevenueByTheater() {
        // Implementation for revenue by theater
        return new ArrayList<>();
    }

    private List<AdminDashboardResponse.RevenueByMovie> generateRevenueByMovie() {
        // Implementation for revenue by movie
        return new ArrayList<>();
    }

    private List<AdminDashboardResponse.UserRegistrationTrend> generateUserRegistrationTrend() {
        // Implementation for user registration trend
        return new ArrayList<>();
    }

    private List<AdminDashboardResponse.TopUsers> generateTopUsers() {
        // Implementation for top users
        return new ArrayList<>();
    }

    private List<AdminDashboardResponse.BookingTrend> generateBookingTrend() {
        // Implementation for booking trend
        return new ArrayList<>();
    }

    private List<AdminDashboardResponse.BookingByTimeSlot> generateBookingByTimeSlot() {
        // Implementation for booking by time slot
        return new ArrayList<>();
    }

    private List<AdminDashboardResponse.BookingByDay> generateBookingByDay() {
        // Implementation for booking by day
        return new ArrayList<>();
    }

    private List<AdminDashboardResponse.TopMovies> generateTopMovies() {
        // Implementation for top movies
        return new ArrayList<>();
    }

    private List<AdminDashboardResponse.MoviePerformance> generateMoviePerformance() {
        // Implementation for movie performance
        return new ArrayList<>();
    }

    private Map<String, Long> generateMoviesByGenre() {
        // Implementation for movies by genre
        return new HashMap<>();
    }

    private Map<String, Long> generateMoviesByLanguage() {
        // Implementation for movies by language
        return new HashMap<>();
    }

    private List<AdminDashboardResponse.TopTheaters> generateTopTheaters() {
        // Implementation for top theaters
        return new ArrayList<>();
    }

    private Map<String, Long> generateTheatersByCity() {
        // Implementation for theaters by city
        return new HashMap<>();
    }

    private Map<String, Long> generateTheatersByState() {
        // Implementation for theaters by state
        return new HashMap<>();
    }

    private Map<String, Long> generateUsersByCity() {
        // Implementation for users by city
        return new HashMap<>();
    }
}

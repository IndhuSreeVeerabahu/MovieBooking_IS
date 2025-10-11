package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.AdminDashboardResponse;
import com.example.MovieTicketBooking.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    /**
     * Get comprehensive admin dashboard data
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse> getDashboardData() {
        try {
            log.info("Generating admin dashboard data");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating admin dashboard data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get dashboard overview
     */
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse.DashboardOverview> getDashboardOverview() {
        try {
            log.info("Getting dashboard overview");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response.getOverview());
        } catch (Exception e) {
            log.error("Error getting dashboard overview", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get revenue analytics
     */
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse.RevenueAnalytics> getRevenueAnalytics() {
        try {
            log.info("Getting revenue analytics");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response.getRevenue());
        } catch (Exception e) {
            log.error("Error getting revenue analytics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user analytics
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse.UserAnalytics> getUserAnalytics() {
        try {
            log.info("Getting user analytics");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response.getUsers());
        } catch (Exception e) {
            log.error("Error getting user analytics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get booking analytics
     */
    @GetMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse.BookingAnalytics> getBookingAnalytics() {
        try {
            log.info("Getting booking analytics");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response.getBookings());
        } catch (Exception e) {
            log.error("Error getting booking analytics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get movie analytics
     */
    @GetMapping("/movies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse.MovieAnalytics> getMovieAnalytics() {
        try {
            log.info("Getting movie analytics");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response.getMovies());
        } catch (Exception e) {
            log.error("Error getting movie analytics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get theater analytics
     */
    @GetMapping("/theaters")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse.TheaterAnalytics> getTheaterAnalytics() {
        try {
            log.info("Getting theater analytics");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response.getTheaters());
        } catch (Exception e) {
            log.error("Error getting theater analytics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get recent activities
     */
    @GetMapping("/activities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<AdminDashboardResponse.RecentActivity>> getRecentActivities() {
        try {
            log.info("Getting recent activities");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response.getRecentActivities());
        } catch (Exception e) {
            log.error("Error getting recent activities", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get revenue chart data
     */
    @GetMapping("/charts/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<AdminDashboardResponse.ChartData>> getRevenueChart() {
        try {
            log.info("Getting revenue chart data");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response.getRevenueChart());
        } catch (Exception e) {
            log.error("Error getting revenue chart data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get booking chart data
     */
    @GetMapping("/charts/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<AdminDashboardResponse.ChartData>> getBookingChart() {
        try {
            log.info("Getting booking chart data");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response.getBookingChart());
        } catch (Exception e) {
            log.error("Error getting booking chart data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user chart data
     */
    @GetMapping("/charts/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<AdminDashboardResponse.ChartData>> getUserChart() {
        try {
            log.info("Getting user chart data");
            AdminDashboardResponse response = adminDashboardService.getDashboardData();
            return ResponseEntity.ok(response.getUserChart());
        } catch (Exception e) {
            log.error("Error getting user chart data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.entity.User;
import com.example.MovieTicketBooking.service.UserService;
import com.example.MovieTicketBooking.service.MovieService;
import com.example.MovieTicketBooking.service.TheaterService;
import com.example.MovieTicketBooking.service.ShowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {
    
    private final UserService userService;
    private final MovieService movieService;
    private final TheaterService theaterService;
    private final ShowService showService;
    
    /**
     * Show login page
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        log.info("Login page requested - error: {}, logout: {}", error, logout);
        if (error != null) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("success", "You have been logged out successfully.");
        }
        return "login";
    }
    
    /**
     * Show registration page
     */
    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "error", required = false) String error,
                              Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "register";
    }
    
    /**
     * Handle user registration
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam(required = false) String phoneNumber,
                              RedirectAttributes redirectAttributes) {
        try {
            log.info("Registration attempt for email: {}", email);
            log.info("Registration data - firstName: {}, lastName: {}, email: {}, phoneNumber: {}", 
                    firstName, lastName, email, phoneNumber);
            
            // Create RegisterRequest object
            com.example.MovieTicketBooking.dto.RegisterRequest request = 
                com.example.MovieTicketBooking.dto.RegisterRequest.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(password)
                    .phoneNumber(phoneNumber)
                    .build();
            
            // Register user
            userService.register(request);
            
            log.info("Registration successful for email: {}", email);
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! Please login with your credentials.");
            return "redirect:/login";
            
        } catch (Exception e) {
            log.error("Registration failed for email: {}", email, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
    
    /**
     * Show user dashboard
     */
    @GetMapping("/dashboard")
    public String dashboardPage(Authentication authentication, Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("user", currentUser);
            return "dashboard";
        } catch (Exception e) {
            log.error("Failed to load dashboard", e);
            return "redirect:/login";
        }
    }
    
    /**
     * Show home page
     */
    @GetMapping("/")
    public String homePage(Authentication authentication, Model model) {
        log.info("Root page requested");
        try {
            // Get featured movies for homepage
            List<com.example.MovieTicketBooking.dto.MovieResponse> featuredMovies = movieService.getFeaturedMovies();
            List<com.example.MovieTicketBooking.dto.MovieResponse> currentlyShowing = movieService.getCurrentlyShowingMovies();
            
            model.addAttribute("featuredMovies", featuredMovies);
            model.addAttribute("currentlyShowing", currentlyShowing);
            
            // Add user information if authenticated
            if (authentication != null && authentication.isAuthenticated()) {
                User currentUser = (User) authentication.getPrincipal();
                model.addAttribute("user", currentUser);
                model.addAttribute("isAuthenticated", true);
            } else {
                model.addAttribute("isAuthenticated", false);
            }
        } catch (Exception e) {
            log.error("Error loading movies for homepage", e);
            model.addAttribute("featuredMovies", new java.util.ArrayList<>());
            model.addAttribute("currentlyShowing", new java.util.ArrayList<>());
            model.addAttribute("isAuthenticated", false);
        }
        return "index";
    }
    
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public String healthCheck() {
        log.info("Health check requested");
        return "Application is healthy!";
    }
    
    /**
     * Debug endpoint to test database connection
     */
    @GetMapping("/debug")
    public String debugPage(Model model) {
        try {
            log.info("Debug page requested");
            long userCount = userService.getAllUsers().size();
            model.addAttribute("userCount", userCount);
            model.addAttribute("status", "Database connection successful");
            return "debug";
        } catch (Exception e) {
            log.error("Debug page error", e);
            model.addAttribute("error", e.getMessage());
            return "debug";
        }
    }
    
    /**
     * Show admin dashboard
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboardPage(Authentication authentication, Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getRole() != User.Role.ADMIN) {
                log.warn("Non-admin user attempted to access admin dashboard: {}", currentUser.getEmail());
                return "redirect:/dashboard";
            }
            model.addAttribute("user", currentUser);
            return "admin-dashboard";
        } catch (Exception e) {
            log.error("Failed to load admin dashboard", e);
            return "redirect:/login";
        }
    }
    
    /**
     * Show user management page
     */
    @GetMapping("/admin/users")
    public String adminUsersPage(Authentication authentication, Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getRole() != User.Role.ADMIN) {
                log.warn("Non-admin user attempted to access user management: {}", currentUser.getEmail());
                return "redirect:/dashboard";
            }
            model.addAttribute("user", currentUser);
            return "admin-users";
        } catch (Exception e) {
            log.error("Failed to load user management page", e);
            return "redirect:/login";
        }
    }
    
    /**
     * Show movie management page
     */
    @GetMapping("/admin/movies")
    public String adminMoviesPage(Authentication authentication, Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getRole() != User.Role.ADMIN) {
                log.warn("Non-admin user attempted to access movie management: {}", currentUser.getEmail());
                return "redirect:/dashboard";
            }
            model.addAttribute("user", currentUser);
            return "admin-movies";
        } catch (Exception e) {
            log.error("Failed to load movie management page", e);
            return "redirect:/login";
        }
    }
    
    /**
     * Show theater management page
     */
    @GetMapping("/admin/theaters")
    public String adminTheatersPage(Authentication authentication, Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getRole() != User.Role.ADMIN) {
                log.warn("Non-admin user attempted to access theater management: {}", currentUser.getEmail());
                return "redirect:/dashboard";
            }
            model.addAttribute("user", currentUser);
            return "admin-theaters";
        } catch (Exception e) {
            log.error("Failed to load theater management page", e);
            return "redirect:/login";
        }
    }
    
    /**
     * Show show management page
     */
    @GetMapping("/admin/shows")
    public String adminShowsPage(Authentication authentication, Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getRole() != User.Role.ADMIN) {
                log.warn("Non-admin user attempted to access show management: {}", currentUser.getEmail());
                return "redirect:/dashboard";
            }
            model.addAttribute("user", currentUser);
            return "admin-shows";
        } catch (Exception e) {
            log.error("Failed to load show management page", e);
            return "redirect:/login";
        }
    }
    
    /**
     * Show movies page for users
     */
    @GetMapping("/movies")
    public String moviesPage(Authentication authentication, Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("user", currentUser);
            
            // Load movie data for the movies page
            List<com.example.MovieTicketBooking.dto.MovieResponse> allMovies = movieService.getAllActiveMovies();
            List<com.example.MovieTicketBooking.dto.MovieResponse> currentlyShowing = movieService.getCurrentlyShowingMovies();
            List<com.example.MovieTicketBooking.dto.MovieResponse> comingSoon = movieService.getComingSoonMovies();
            List<com.example.MovieTicketBooking.dto.MovieResponse> featuredMovies = movieService.getFeaturedMovies();
            
            model.addAttribute("allMovies", allMovies);
            model.addAttribute("currentlyShowing", currentlyShowing);
            model.addAttribute("comingSoon", comingSoon);
            model.addAttribute("featuredMovies", featuredMovies);
            
            return "movies";
        } catch (Exception e) {
            log.error("Failed to load movies page", e);
            return "redirect:/login";
        }
    }
    
    /**
     * Show booking page
     */
    @GetMapping("/booking")
    public String bookingPage(@RequestParam Long showId, Authentication authentication, Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("user", currentUser);
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("showId", showId);
            
            // Add CSRF token if available
            try {
                org.springframework.security.web.csrf.CsrfToken csrfToken = 
                    (org.springframework.security.web.csrf.CsrfToken) 
                    org.springframework.web.context.request.RequestContextHolder
                        .currentRequestAttributes()
                        .getAttribute("_csrf", org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);
                if (csrfToken != null) {
                    model.addAttribute("_csrf", csrfToken);
                }
            } catch (Exception csrfException) {
                log.debug("CSRF token not available: {}", csrfException.getMessage());
            }
            
            return "booking";
        } catch (Exception e) {
            log.error("Failed to load booking page", e);
            return "redirect:/movies";
        }
    }
    
    /**
     * Show user bookings page
     */
    @GetMapping("/bookings")
    public String bookingsPage(Authentication authentication, Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("user", currentUser);
            return "bookings";
        } catch (Exception e) {
            log.error("Failed to load bookings page", e);
            return "redirect:/login";
        }
    }
    
    /**
     * Show booking confirmation page
     */
    @GetMapping("/booking/confirmation")
    public String bookingConfirmationPage(@RequestParam String bookingReference, 
                                        Authentication authentication, 
                                        Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("user", currentUser);
            model.addAttribute("bookingReference", bookingReference);
            return "booking-confirmation";
        } catch (Exception e) {
            log.error("Failed to load booking confirmation page", e);
            return "redirect:/bookings";
        }
    }

    /**
     * Show user settings page
     */
    @GetMapping("/settings")
    public String settingsPage(Model model, Authentication authentication) {
        try {
            log.info("Loading settings page");
            
            if (authentication != null && authentication.isAuthenticated()) {
                var user = userService.getUserByEmail(authentication.getName());
                model.addAttribute("user", user);
                model.addAttribute("isAuthenticated", true);
            } else {
                model.addAttribute("isAuthenticated", false);
            }
            
            return "settings";
        } catch (Exception e) {
            log.error("Failed to load settings page", e);
            return "redirect:/login";
        }
    }

    /**
     * Show theaters page
     */
    @GetMapping("/theaters")
    public String theatersPage(Authentication authentication, Model model) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("user", currentUser);
            return "theaters";
        } catch (Exception e) {
            log.error("Failed to load theaters page", e);
            return "redirect:/login";
        }
    }

    /**
     * Show movie details page
     */
    @GetMapping("/movie/{movieId}")
    public String movieDetailsPage(@PathVariable Long movieId, Authentication authentication, Model model) {
        try {
            log.info("Loading movie details page for movie ID: {}", movieId);
            
            // Add user to model
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("user", currentUser);
            
            // Get movie details
            var movie = movieService.getMovieById(movieId);
            model.addAttribute("movie", movie);
            
            // Get theaters with shows for this movie
            var theaters = theaterService.getAllActiveTheaters();
            var theatersWithShows = theaters.stream()
                    .map(theater -> {
                        try {
                            var shows = showService.getShowsByMovieAndTheater(movieId, theater.getId());
                            theater.setShows(shows);
                            return theater;
                        } catch (Exception e) {
                            log.warn("Failed to get shows for theater {} and movie {}", theater.getId(), movieId);
                            return theater;
                        }
                    })
                    .filter(theater -> theater.getShows() != null && !theater.getShows().isEmpty())
                    .collect(java.util.stream.Collectors.toList());
            
            model.addAttribute("theatersWithShows", theatersWithShows);
            
            // Get similar movies (same genre)
            var similarMovies = movieService.getMoviesByGenre(movie.getGenre())
                    .stream()
                    .filter(m -> !m.getId().equals(movieId))
                    .limit(6)
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("similarMovies", similarMovies);
            
            // Generate available dates (next 7 days)
            var availableDates = java.time.LocalDate.now()
                    .datesUntil(java.time.LocalDate.now().plusDays(7))
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("availableDates", availableDates);
            
            return "movie-details";
        } catch (Exception e) {
            log.error("Failed to load movie details page for movie ID: {}", movieId, e);
            return "redirect:/movies";
        }
    }
}

package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.entity.*;
import com.example.MovieTicketBooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowRepository showRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // User Export Methods
    public byte[] exportUsersToExcel() throws IOException {
        // For now, return CSV data as bytes - Excel support will be added after Maven dependencies are resolved
        String csvData = exportUsersToCSV();
        return csvData.getBytes("UTF-8");
    }

    public String exportUsersToCSV() throws IOException {
        List<User> users = userRepository.findAll();
        
        StringBuilder csv = new StringBuilder();
        
        // Write header
        csv.append("ID,First Name,Last Name,Email,Phone Number,Role,Status,Created At,Updated At\n");
        
        // Write data
        for (User user : users) {
            csv.append(escapeCsvField(user.getId().toString())).append(",");
            csv.append(escapeCsvField(user.getFirstName())).append(",");
            csv.append(escapeCsvField(user.getLastName())).append(",");
            csv.append(escapeCsvField(user.getEmail())).append(",");
            csv.append(escapeCsvField(user.getPhoneNumber() != null ? user.getPhoneNumber() : "")).append(",");
            csv.append(escapeCsvField(user.getRole().name())).append(",");
            csv.append(escapeCsvField(user.getIsEnabled() ? "Active" : "Disabled")).append(",");
            csv.append(escapeCsvField(user.getCreatedAt().format(DATE_FORMATTER))).append(",");
            csv.append(escapeCsvField(user.getUpdatedAt() != null ? user.getUpdatedAt().format(DATE_FORMATTER) : "")).append("\n");
        }
        
        return csv.toString();
    }

    // Movie Export Methods
    public byte[] exportMoviesToExcel() throws IOException {
        String csvData = exportMoviesToCSV();
        return csvData.getBytes("UTF-8");
    }

    public String exportMoviesToCSV() throws IOException {
        List<Movie> movies = movieRepository.findAll();
        
        StringBuilder csv = new StringBuilder();
        
        // Write header
        csv.append("ID,Title,Description,Genre,Duration (min),Rating,Director,Cast,Language,Release Date,End Date,Active,Featured,Created At\n");
        
        // Write data
        for (Movie movie : movies) {
            csv.append(escapeCsvField(movie.getId().toString())).append(",");
            csv.append(escapeCsvField(movie.getTitle())).append(",");
            csv.append(escapeCsvField(movie.getDescription() != null ? movie.getDescription() : "")).append(",");
            csv.append(escapeCsvField(movie.getGenre().getDisplayName())).append(",");
            csv.append(escapeCsvField(movie.getDurationMinutes().toString())).append(",");
            csv.append(escapeCsvField(movie.getRating().getDisplayName())).append(",");
            csv.append(escapeCsvField(movie.getDirector() != null ? movie.getDirector() : "")).append(",");
            csv.append(escapeCsvField(movie.getCast() != null ? movie.getCast() : "")).append(",");
            csv.append(escapeCsvField(movie.getLanguage() != null ? movie.getLanguage() : "")).append(",");
            csv.append(escapeCsvField(movie.getReleaseDate() != null ? movie.getReleaseDate().format(DATE_FORMATTER) : "")).append(",");
            csv.append(escapeCsvField(movie.getEndDate() != null ? movie.getEndDate().format(DATE_FORMATTER) : "")).append(",");
            csv.append(escapeCsvField(movie.getIsActive() ? "Yes" : "No")).append(",");
            csv.append(escapeCsvField(movie.getIsFeatured() ? "Yes" : "No")).append(",");
            csv.append(escapeCsvField(movie.getCreatedAt().format(DATE_FORMATTER))).append("\n");
        }
        
        return csv.toString();
    }

    // Booking Export Methods
    public byte[] exportBookingsToExcel() throws IOException {
        String csvData = exportBookingsToCSV();
        return csvData.getBytes("UTF-8");
    }

    public String exportBookingsToCSV() throws IOException {
        List<Booking> bookings = bookingRepository.findAll();
        
        StringBuilder csv = new StringBuilder();
        
        // Write header
        csv.append("ID,Booking Reference,User Email,Movie Title,Theater Name,Show Date,Total Amount,Booking Status,Payment Status,Payment Method,Booking Date,Created At\n");
        
        // Write data
        for (Booking booking : bookings) {
            csv.append(escapeCsvField(booking.getId().toString())).append(",");
            csv.append(escapeCsvField(booking.getBookingReference())).append(",");
            csv.append(escapeCsvField(booking.getUser().getEmail())).append(",");
            csv.append(escapeCsvField(booking.getShow().getMovie().getTitle())).append(",");
            csv.append(escapeCsvField(booking.getShow().getTheater().getName())).append(",");
            csv.append(escapeCsvField(booking.getShow().getShowDateTime().format(DATE_FORMATTER))).append(",");
            csv.append(escapeCsvField(booking.getTotalAmount().toString())).append(",");
            csv.append(escapeCsvField(booking.getBookingStatus().getDisplayName())).append(",");
            csv.append(escapeCsvField(booking.getPaymentStatus().getDisplayName())).append(",");
            csv.append(escapeCsvField(booking.getPaymentMethod() != null ? booking.getPaymentMethod() : "")).append(",");
            csv.append(escapeCsvField(booking.getBookingDate().format(DATE_FORMATTER))).append(",");
            csv.append(escapeCsvField(booking.getCreatedAt().format(DATE_FORMATTER))).append("\n");
        }
        
        return csv.toString();
    }

    // Theater Export Methods
    public byte[] exportTheatersToExcel() throws IOException {
        String csvData = exportTheatersToCSV();
        return csvData.getBytes("UTF-8");
    }

    public String exportTheatersToCSV() throws IOException {
        List<Theater> theaters = theaterRepository.findAll();
        
        StringBuilder csv = new StringBuilder();
        
        // Write header
        csv.append("ID,Name,Address,City,State,Pincode,Phone Number,Email,Total Screens,Active,Created At,Updated At\n");
        
        // Write data
        for (Theater theater : theaters) {
            csv.append(escapeCsvField(theater.getId().toString())).append(",");
            csv.append(escapeCsvField(theater.getName())).append(",");
            csv.append(escapeCsvField(theater.getAddress())).append(",");
            csv.append(escapeCsvField(theater.getCity())).append(",");
            csv.append(escapeCsvField(theater.getState())).append(",");
            csv.append(escapeCsvField(theater.getPincode())).append(",");
            csv.append(escapeCsvField(theater.getPhoneNumber() != null ? theater.getPhoneNumber() : "")).append(",");
            csv.append(escapeCsvField(theater.getEmail() != null ? theater.getEmail() : "")).append(",");
            csv.append(escapeCsvField(theater.getScreens() != null ? String.valueOf(theater.getScreens().size()) : "0")).append(",");
            csv.append(escapeCsvField(theater.getIsActive() ? "Yes" : "No")).append(",");
            csv.append(escapeCsvField(theater.getCreatedAt().format(DATE_FORMATTER))).append(",");
            csv.append(escapeCsvField(theater.getUpdatedAt() != null ? theater.getUpdatedAt().format(DATE_FORMATTER) : "")).append("\n");
        }
        
        return csv.toString();
    }

    // Show Export Methods
    public byte[] exportShowsToExcel() throws IOException {
        String csvData = exportShowsToCSV();
        return csvData.getBytes("UTF-8");
    }

    public String exportShowsToCSV() throws IOException {
        List<Show> shows = showRepository.findAll();
        
        StringBuilder csv = new StringBuilder();
        
        // Write header
        csv.append("ID,Movie Title,Theater Name,Screen Name,Show Date,Show Time,Base Price,Premium Price,VIP Price,Show Status,Active,Created At\n");
        
        // Write data
        for (Show show : shows) {
            csv.append(escapeCsvField(show.getId().toString())).append(",");
            csv.append(escapeCsvField(show.getMovie().getTitle())).append(",");
            csv.append(escapeCsvField(show.getTheater().getName())).append(",");
            csv.append(escapeCsvField(show.getScreen().getScreenDisplayName())).append(",");
            csv.append(escapeCsvField(show.getShowDate().toString())).append(",");
            csv.append(escapeCsvField(show.getShowTime().toString())).append(",");
            csv.append(escapeCsvField(show.getBasePrice().toString())).append(",");
            csv.append(escapeCsvField(show.getPremiumPrice() != null ? show.getPremiumPrice().toString() : "")).append(",");
            csv.append(escapeCsvField(show.getVipPrice() != null ? show.getVipPrice().toString() : "")).append(",");
            csv.append(escapeCsvField(show.getShowStatus().getDisplayName())).append(",");
            csv.append(escapeCsvField(show.getIsActive() ? "Yes" : "No")).append(",");
            csv.append(escapeCsvField(show.getCreatedAt().format(DATE_FORMATTER))).append("\n");
        }
        
        return csv.toString();
    }
    
    private String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}

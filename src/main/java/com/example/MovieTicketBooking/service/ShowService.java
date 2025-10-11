package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.ShowRequest;
import com.example.MovieTicketBooking.dto.ShowResponse;
import com.example.MovieTicketBooking.entity.*;
import com.example.MovieTicketBooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;

    /**
     * Create a new show
     */
    public ShowResponse createShow(ShowRequest request) {
        log.info("Creating show for movie: {} at theater: {}", request.getMovieId(), request.getTheaterId());
        
        // Validate movie exists and is active
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + request.getMovieId()));
        
        if (!movie.getIsActive()) {
            throw new RuntimeException("Cannot create show for inactive movie");
        }

        // Validate theater exists and is active
        Theater theater = theaterRepository.findById(request.getTheaterId())
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + request.getTheaterId()));
        
        if (!theater.getIsActive()) {
            throw new RuntimeException("Cannot create show for inactive theater");
        }

        // Validate screen exists and belongs to theater
        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new RuntimeException("Screen not found with ID: " + request.getScreenId()));
        
        if (!screen.getTheater().getId().equals(theater.getId())) {
            throw new RuntimeException("Screen does not belong to the specified theater");
        }

        // Check for time conflicts
        LocalDate showDate = request.getShowDate();
        LocalTime showTime = request.getShowTime();
        LocalTime endTime = request.getEndTime();
        
        if (endTime.isBefore(showTime) || endTime.equals(showTime)) {
            throw new RuntimeException("End time (" + endTime + ") must be after start time (" + showTime + ")");
        }

        // Check for overlapping shows on the same screen
        List<Show> conflictingShows = showRepository.findConflictingShows(
                screen.getId(), showDate, showTime, endTime);
        
        if (!conflictingShows.isEmpty()) {
            throw new RuntimeException("Show time conflicts with existing show on the same screen");
        }

        Show show = Show.builder()
                .movie(movie)
                .theater(theater)
                .screen(screen)
                .showDate(showDate)
                .showTime(showTime)
                .endTime(endTime)
                .basePrice(request.getBasePrice())
                .premiumPrice(request.getPremiumPrice())
                .vipPrice(request.getVipPrice())
                .showStatus(Show.ShowStatus.ACTIVE)
                .isActive(true)
                .build();

        Show savedShow = showRepository.save(show);
        log.info("Show created successfully with ID: {}", savedShow.getId());
        
        return mapToShowResponse(savedShow);
    }

    /**
     * Get shows by movie
     */
    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsByMovie(Long movieId) {
        log.info("Fetching shows for movie: {}", movieId);
        List<Show> shows = showRepository.findByMovieIdAndIsActiveTrueOrderByShowDateAscShowTimeAsc(movieId);
        return shows.stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get shows by theater
     */
    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsByTheater(Long theaterId) {
        log.info("Fetching shows for theater: {}", theaterId);
        List<Show> shows = showRepository.findByTheaterIdAndIsActiveTrueOrderByShowDateAscShowTimeAsc(theaterId);
        return shows.stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get shows by movie and theater
     */
    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsByMovieAndTheater(Long movieId, Long theaterId) {
        log.info("Fetching shows for movie: {} at theater: {}", movieId, theaterId);
        List<Show> shows = showRepository.findByMovieIdAndTheaterIdAndIsActiveTrueOrderByShowDateAscShowTimeAsc(movieId, theaterId);
        return shows.stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get shows by date range
     */
    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching shows from {} to {}", startDate, endDate);
        List<Show> shows = showRepository.findByShowDateBetweenAndIsActiveTrueOrderByShowDateAscShowTimeAsc(startDate, endDate);
        return shows.stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get show by ID
     */
    @Transactional(readOnly = true)
    public ShowResponse getShowById(Long showId) {
        log.info("Fetching show by ID: {}", showId);
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));
        return mapToShowResponse(show);
    }

    /**
     * Update show
     */
    public ShowResponse updateShow(Long showId, ShowRequest request) {
        log.info("Updating show: {}", showId);
        
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));

        // Validate movie exists and is active
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + request.getMovieId()));
        
        if (!movie.getIsActive()) {
            throw new RuntimeException("Cannot update show to inactive movie");
        }

        // Validate theater exists and is active
        Theater theater = theaterRepository.findById(request.getTheaterId())
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + request.getTheaterId()));
        
        if (!theater.getIsActive()) {
            throw new RuntimeException("Cannot update show to inactive theater");
        }

        // Validate screen exists and belongs to theater
        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new RuntimeException("Screen not found with ID: " + request.getScreenId()));
        
        if (!screen.getTheater().getId().equals(theater.getId())) {
            throw new RuntimeException("Screen does not belong to the specified theater");
        }

        // Check for time conflicts (excluding current show)
        LocalDate showDate = request.getShowDate();
        LocalTime showTime = request.getShowTime();
        LocalTime endTime = request.getEndTime();
        
        if (endTime.isBefore(showTime) || endTime.equals(showTime)) {
            throw new RuntimeException("End time (" + endTime + ") must be after start time (" + showTime + ")");
        }

        List<Show> conflictingShows = showRepository.findConflictingShowsExcluding(
                screen.getId(), showDate, showTime, endTime, showId);
        
        if (!conflictingShows.isEmpty()) {
            throw new RuntimeException("Show time conflicts with existing show on the same screen");
        }

        show.setMovie(movie);
        show.setTheater(theater);
        show.setScreen(screen);
        show.setShowDate(showDate);
        show.setShowTime(showTime);
        show.setEndTime(endTime);
        show.setBasePrice(request.getBasePrice());
        show.setPremiumPrice(request.getPremiumPrice());
        show.setVipPrice(request.getVipPrice());

        Show updatedShow = showRepository.save(show);
        log.info("Show updated successfully: {}", showId);
        
        return mapToShowResponse(updatedShow);
    }

    /**
     * Toggle show status
     */
    public ShowResponse toggleShowStatus(Long showId) {
        log.info("Toggling show status: {}", showId);
        
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));

        show.setIsActive(!show.getIsActive());
        Show updatedShow = showRepository.save(show);
        
        log.info("Show status toggled to: {} for show: {}", 
                updatedShow.getIsActive(), showId);
        
        return mapToShowResponse(updatedShow);
    }

    /**
     * Update show status
     */
    public ShowResponse updateShowStatus(Long showId, Show.ShowStatus status) {
        log.info("Updating show status: {} to {}", showId, status);
        
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));

        show.setShowStatus(status);
        Show updatedShow = showRepository.save(show);
        
        log.info("Show status updated to: {} for show: {}", 
                updatedShow.getShowStatus(), showId);
        
        return mapToShowResponse(updatedShow);
    }

    /**
     * Delete show
     */
    public void deleteShow(Long showId) {
        log.info("Deleting show: {}", showId);
        
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));

        // Check if show has bookings
        if (show.getBookings() != null && !show.getBookings().isEmpty()) {
            boolean hasConfirmedBookings = show.getBookings().stream()
                    .anyMatch(booking -> booking.getBookingStatus() == Booking.BookingStatus.CONFIRMED);
            if (hasConfirmedBookings) {
                throw new RuntimeException("Cannot delete show with confirmed bookings");
            }
        }

        showRepository.delete(show);
        log.info("Show deleted successfully: {}", showId);
    }

    /**
     * Get all shows with pagination
     */
    @Transactional(readOnly = true)
    public List<ShowResponse> getAllShows(int page, int size) {
        log.info("Fetching all shows - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<Show> shows = showRepository.findAll(pageable).getContent();
        return shows.stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all shows without pagination (for admin)
     */
    @Transactional(readOnly = true)
    public List<ShowResponse> getAllShowsForAdmin() {
        log.info("Fetching all shows for admin");
        List<Show> shows = showRepository.findAll();
        log.info("Found {} shows in database", shows.size());
        return shows.stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map Show entity to ShowResponse DTO
     */
    public ShowResponse mapToShowResponse(Show show) {
        try {
            return ShowResponse.builder()
                    .id(show.getId())
                    .movieId(show.getMovie() != null ? show.getMovie().getId() : null)
                    .movieTitle(show.getMovie() != null ? show.getMovie().getTitle() : "Unknown Movie")
                    .theaterId(show.getTheater() != null ? show.getTheater().getId() : null)
                    .theaterName(show.getTheater() != null ? show.getTheater().getName() : "Unknown Theater")
                    .screenId(show.getScreen() != null ? show.getScreen().getId() : null)
                    .screenName(show.getScreen() != null ? show.getScreen().getScreenDisplayName() : "Unknown Screen")
                    .showDate(show.getShowDate())
                    .showTime(show.getShowTime())
                    .endTime(show.getEndTime())
                    .basePrice(show.getBasePrice())
                    .premiumPrice(show.getPremiumPrice())
                    .vipPrice(show.getVipPrice())
                    .showStatus(show.getShowStatus())
                    .isActive(show.getIsActive())
                    .createdAt(show.getCreatedAt())
                    .updatedAt(show.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping show to response for show ID: {}", show.getId(), e);
            return ShowResponse.builder()
                    .id(show.getId())
                    .movieTitle("Error loading show")
                    .theaterName("Error loading theater")
                    .screenName("Error loading screen")
                    .showDate(show.getShowDate())
                    .showTime(show.getShowTime())
                    .endTime(show.getEndTime())
                    .basePrice(show.getBasePrice())
                    .premiumPrice(show.getPremiumPrice())
                    .vipPrice(show.getVipPrice())
                    .showStatus(show.getShowStatus())
                    .isActive(show.getIsActive())
                    .createdAt(show.getCreatedAt())
                    .updatedAt(show.getUpdatedAt())
                    .build();
        }
    }
}

package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.TheaterRequest;
import com.example.MovieTicketBooking.dto.TheaterResponse;
import com.example.MovieTicketBooking.dto.ScreenResponse;
import com.example.MovieTicketBooking.entity.Theater;
import com.example.MovieTicketBooking.entity.Screen;
import com.example.MovieTicketBooking.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TheaterService {

    private final TheaterRepository theaterRepository;

    /**
     * Create a new theater
     */
    public TheaterResponse createTheater(TheaterRequest request) {
        log.info("Creating theater: {}", request.getName());
        
        Theater theater = Theater.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .totalScreens(request.getTotalScreens())
                .amenities(request.getAmenities())
                .isActive(true)
                .build();

        Theater savedTheater = theaterRepository.save(theater);
        log.info("Theater created successfully with ID: {}", savedTheater.getId());
        
        return mapToTheaterResponse(savedTheater);
    }

    /**
     * Get all active theaters
     */
    @Transactional(readOnly = true)
    public List<TheaterResponse> getAllActiveTheaters() {
        log.info("Fetching all active theaters");
        List<Theater> theaters = theaterRepository.findByIsActiveTrueWithScreens();
        return theaters.stream()
                .map(this::mapToTheaterResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all cities from theaters
     */
    @Transactional(readOnly = true)
    public List<String> getAllCities() {
        log.info("Fetching all cities from theaters");
        List<Theater> theaters = theaterRepository.findByIsActiveTrue();
        return theaters.stream()
                .map(Theater::getCity)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get theaters by city
     */
    @Transactional(readOnly = true)
    public List<TheaterResponse> getTheatersByCity(String city) {
        log.info("Fetching theaters for city: {}", city);
        List<Theater> theaters = theaterRepository.findByCityIgnoreCaseAndIsActiveTrue(city);
        return theaters.stream()
                .map(this::mapToTheaterResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get theater by ID
     */
    @Transactional(readOnly = true)
    public TheaterResponse getTheaterById(Long theaterId) {
        log.info("Fetching theater by ID: {}", theaterId);
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + theaterId));
        return mapToTheaterResponse(theater);
    }

    /**
     * Update theater
     */
    public TheaterResponse updateTheater(Long theaterId, TheaterRequest request) {
        log.info("Updating theater: {}", theaterId);
        
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + theaterId));

        theater.setName(request.getName());
        theater.setAddress(request.getAddress());
        theater.setCity(request.getCity());
        theater.setState(request.getState());
        theater.setPincode(request.getPincode());
        theater.setPhoneNumber(request.getPhoneNumber());
        theater.setEmail(request.getEmail());
        theater.setTotalScreens(request.getTotalScreens());
        theater.setAmenities(request.getAmenities());
        
        // Note: isActive is handled separately via toggle-status endpoint
        // This ensures proper audit trail and prevents accidental status changes

        Theater updatedTheater = theaterRepository.save(theater);
        log.info("Theater updated successfully: {}", theaterId);
        
        return mapToTheaterResponse(updatedTheater);
    }

    /**
     * Toggle theater status
     */
    public TheaterResponse toggleTheaterStatus(Long theaterId) {
        log.info("Toggling theater status: {}", theaterId);
        
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + theaterId));

        theater.setIsActive(!theater.getIsActive());
        Theater updatedTheater = theaterRepository.save(theater);
        
        log.info("Theater status toggled to: {} for theater: {}", 
                updatedTheater.getIsActive(), theaterId);
        
        return mapToTheaterResponse(updatedTheater);
    }

    /**
     * Delete theater
     */
    public void deleteTheater(Long theaterId) {
        log.info("Deleting theater: {}", theaterId);
        
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + theaterId));

        // Check if theater has active shows using a direct query
        // This avoids lazy loading issues
        boolean hasActiveShows = theaterRepository.existsByIdAndShowsIsActiveTrue(theaterId);
        if (hasActiveShows) {
            throw new RuntimeException("Cannot delete theater with active shows. Please deactivate all shows first.");
        }

        // Check if theater has any screens
        boolean hasScreens = theaterRepository.existsByIdAndScreensIsActiveTrue(theaterId);
        if (hasScreens) {
            throw new RuntimeException("Cannot delete theater with active screens. Please deactivate all screens first.");
        }

        theaterRepository.delete(theater);
        log.info("Theater deleted successfully: {}", theaterId);
    }

    /**
     * Get all theaters with pagination
     */
    @Transactional(readOnly = true)
    public List<TheaterResponse> getAllTheaters(int page, int size) {
        log.info("Fetching all theaters - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<Theater> theaters = theaterRepository.findAll(pageable).getContent();
        return theaters.stream()
                .map(this::mapToTheaterResponse)
                .collect(Collectors.toList());
    }

    /**
     * Check if theater can be deleted
     */
    @Transactional(readOnly = true)
    public Map<String, Object> checkTheaterDeletionStatus(Long theaterId) {
        log.info("Checking deletion status for theater: {}", theaterId);
        
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + theaterId));

        Map<String, Object> result = new HashMap<>();
        result.put("theaterId", theaterId);
        result.put("theaterName", theater.getName());
        
        // Check for active shows
        boolean hasActiveShows = theaterRepository.existsByIdAndShowsIsActiveTrue(theaterId);
        result.put("hasActiveShows", hasActiveShows);
        
        // Check for active screens
        boolean hasActiveScreens = theaterRepository.existsByIdAndScreensIsActiveTrue(theaterId);
        result.put("hasActiveScreens", hasActiveScreens);
        
        // Determine if can delete
        boolean canDelete = !hasActiveShows && !hasActiveScreens;
        result.put("canDelete", canDelete);
        
        if (canDelete) {
            result.put("message", "Theater can be deleted safely");
        } else {
            StringBuilder message = new StringBuilder("Cannot delete theater because it has: ");
            if (hasActiveShows) {
                message.append("active shows");
            }
            if (hasActiveShows && hasActiveScreens) {
                message.append(" and ");
            }
            if (hasActiveScreens) {
                message.append("active screens");
            }
            message.append(". Please deactivate all shows and screens first.");
            result.put("message", message.toString());
        }
        
        return result;
    }

    /**
     * Map Theater entity to TheaterResponse DTO
     */
    public TheaterResponse mapToTheaterResponse(Theater theater) {
        List<ScreenResponse> screenResponses = null;
        if (theater.getScreens() != null) {
            screenResponses = theater.getScreens().stream()
                    .map(this::mapToScreenResponse)
                    .collect(Collectors.toList());
        }
        
        return TheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .address(theater.getAddress())
                .city(theater.getCity())
                .state(theater.getState())
                .pincode(theater.getPincode())
                .phoneNumber(theater.getPhoneNumber())
                .email(theater.getEmail())
                .totalScreens(theater.getTotalScreens())
                .amenities(theater.getAmenities())
                .isActive(theater.getIsActive())
                .fullAddress(theater.getFullAddress())
                .screens(screenResponses)
                .createdAt(theater.getCreatedAt())
                .updatedAt(theater.getUpdatedAt())
                .build();
    }
    
    /**
     * Map Theater entity to TheaterResponse DTO with shows
     */
    public TheaterResponse mapToTheaterResponseWithShows(Theater theater, List<com.example.MovieTicketBooking.dto.ShowResponse> shows) {
        TheaterResponse response = mapToTheaterResponse(theater);
        // Add shows to the response - this will be handled by the WebController
        return response;
    }
    
    /**
     * Get screens for a theater
     */
    @Transactional(readOnly = true)
    public List<ScreenResponse> getTheaterScreens(Long theaterId) {
        log.info("Fetching screens for theater: {}", theaterId);
        
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + theaterId));
        
        return theater.getScreens().stream()
                .filter(Screen::getIsActive)
                .map(this::mapToScreenResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map Screen entity to ScreenResponse DTO
     */
    private ScreenResponse mapToScreenResponse(Screen screen) {
        return ScreenResponse.builder()
                .id(screen.getId())
                .theaterId(screen.getTheater().getId())
                .screenNumber(screen.getScreenNumber())
                .screenName(screen.getScreenName())
                .totalSeats(screen.getTotalSeats())
                .screenType(screen.getScreenType().name())
                .isActive(screen.getIsActive())
                .createdAt(screen.getCreatedAt())
                .updatedAt(screen.getUpdatedAt())
                .build();
    }
}

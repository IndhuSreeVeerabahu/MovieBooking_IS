package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.*;
import com.example.MovieTicketBooking.entity.Theater;
import com.example.MovieTicketBooking.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TheaterSearchService {

    private final TheaterRepository theaterRepository;
    private final TheaterService theaterService;

    /**
     * Search theaters with advanced filtering
     */
    public TheaterSearchResponse searchTheaters(TheaterSearchRequest request) {
        log.info("Searching theaters with filters: {}", request);

        // Build specification for filtering
        Specification<Theater> spec = buildTheaterSpecification(request);

        // Build pageable for pagination and sorting
        Pageable pageable = buildPageable(request);

        // Execute search
        Page<Theater> theaterPage = theaterRepository.findAll(spec, pageable);

        // Convert to response DTOs
        List<TheaterResponse> theaterResponses = theaterPage.getContent().stream()
                .map(theaterService::mapToTheaterResponse)
                .collect(Collectors.toList());

        // Build response
        return TheaterSearchResponse.builder()
                .theaters(theaterResponses)
                .totalElements(theaterPage.getTotalElements())
                .totalPages(theaterPage.getTotalPages())
                .currentPage(theaterPage.getNumber())
                .pageSize(theaterPage.getSize())
                .hasNext(theaterPage.hasNext())
                .hasPrevious(theaterPage.hasPrevious())
                .availableCities(getAvailableCities())
                .availableStates(getAvailableStates())
                .availableAmenities(getAvailableAmenities())
                .appliedFilters(buildAppliedFilters(request))
                .build();
    }

    /**
     * Get theaters by city
     */
    public TheaterSearchResponse getTheatersByCity(String city) {
        log.info("Getting theaters for city: {}", city);

        TheaterSearchRequest request = TheaterSearchRequest.builder()
                .city(city)
                .isActive(true)
                .page(0)
                .size(20)
                .sortBy("name")
                .sortOrder("ASC")
                .build();

        return searchTheaters(request);
    }

    /**
     * Get theaters by state
     */
    public TheaterSearchResponse getTheatersByState(String state) {
        log.info("Getting theaters for state: {}", state);

        TheaterSearchRequest request = TheaterSearchRequest.builder()
                .state(state)
                .isActive(true)
                .page(0)
                .size(20)
                .sortBy("name")
                .sortOrder("ASC")
                .build();

        return searchTheaters(request);
    }

    /**
     * Get theaters by location (latitude/longitude)
     */
    public TheaterSearchResponse getTheatersByLocation(Double latitude, Double longitude, Double radius) {
        log.info("Getting theaters near location: {}, {} within {} km", latitude, longitude, radius);

        // For now, return all active theaters
        // In a real implementation, you would calculate distance using Haversine formula
        TheaterSearchRequest request = TheaterSearchRequest.builder()
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius)
                .isActive(true)
                .page(0)
                .size(20)
                .sortBy("name")
                .sortOrder("ASC")
                .build();

        return searchTheaters(request);
    }

    /**
     * Get theaters with specific amenities
     */
    public TheaterSearchResponse getTheatersByAmenities(List<String> amenities) {
        log.info("Getting theaters with amenities: {}", amenities);

        TheaterSearchRequest request = TheaterSearchRequest.builder()
                .amenities(amenities)
                .isActive(true)
                .page(0)
                .size(20)
                .sortBy("name")
                .sortOrder("ASC")
                .build();

        return searchTheaters(request);
    }

    /**
     * Build theater specification for filtering
     */
    private Specification<Theater> buildTheaterSpecification(TheaterSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Name filter
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + request.getName().toLowerCase() + "%"
                ));
            }

            // City filter
            if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("city")),
                        "%" + request.getCity().toLowerCase() + "%"
                ));
            }

            // State filter
            if (request.getState() != null && !request.getState().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("state")),
                        "%" + request.getState().toLowerCase() + "%"
                ));
            }

            // Pincode filter
            if (request.getPincode() != null && !request.getPincode().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("pincode"),
                        "%" + request.getPincode() + "%"
                ));
            }

            // Amenities filter
            if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
                List<Predicate> amenityPredicates = new ArrayList<>();
                for (String amenity : request.getAmenities()) {
                    amenityPredicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("amenities")),
                            "%" + amenity.toLowerCase() + "%"
                    ));
                }
                predicates.add(criteriaBuilder.or(amenityPredicates.toArray(new Predicate[0])));
            }

            // Active filter
            if (request.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), request.getIsActive()));
            }

            // Location-based filtering would be implemented here
            // This would require additional fields in the Theater entity for latitude/longitude

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Build pageable for pagination and sorting
     */
    private Pageable buildPageable(TheaterSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;

        Sort sort = buildSort(request);
        return PageRequest.of(page, size, sort);
    }

    /**
     * Build sort specification
     */
    private Sort buildSort(TheaterSearchRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "name";
        String sortOrder = request.getSortOrder() != null ? request.getSortOrder() : "ASC";

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(direction, sortBy);
    }

    /**
     * Get available cities
     */
    private List<String> getAvailableCities() {
        return theaterRepository.findDistinctCities();
    }

    /**
     * Get available states
     */
    private List<String> getAvailableStates() {
        return theaterRepository.findDistinctStates();
    }

    /**
     * Get available amenities
     */
    private List<String> getAvailableAmenities() {
        // This would require parsing the amenities JSON field
        // For now, return a static list
        return List.of(
                "Parking", "Food Court", "IMAX", "Dolby Atmos", "3D", 
                "Wheelchair Access", "Online Booking", "Valet Parking",
                "ATM", "WiFi", "Air Conditioning", "Premium Seating"
        );
    }

    /**
     * Build applied filters
     */
    private TheaterSearchResponse.TheaterSearchFilters buildAppliedFilters(TheaterSearchRequest request) {
        return TheaterSearchResponse.TheaterSearchFilters.builder()
                .name(request.getName())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .amenities(request.getAmenities())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();
    }
}

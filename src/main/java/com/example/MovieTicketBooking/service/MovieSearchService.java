package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.*;
import com.example.MovieTicketBooking.entity.Movie;
import com.example.MovieTicketBooking.entity.Show;
import com.example.MovieTicketBooking.repository.MovieRepository;
import com.example.MovieTicketBooking.repository.ShowRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MovieSearchService {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final MovieService movieService;

    /**
     * Search movies with advanced filtering
     */
    public MovieSearchResponse searchMovies(MovieSearchRequest request) {
        log.info("Searching movies with filters: {}", request);

        // Build specification for filtering
        Specification<Movie> spec = buildMovieSpecification(request);

        // Build pageable for pagination and sorting
        Pageable pageable = buildPageable(request);

        // Execute search
        Page<Movie> moviePage = movieRepository.findAll(spec, pageable);

        // Convert to response DTOs
        List<MovieResponse> movieResponses = moviePage.getContent().stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());

        // Build response
        return MovieSearchResponse.builder()
                .movies(movieResponses)
                .totalElements(moviePage.getTotalElements())
                .totalPages(moviePage.getTotalPages())
                .currentPage(moviePage.getNumber())
                .pageSize(moviePage.getSize())
                .hasNext(moviePage.hasNext())
                .hasPrevious(moviePage.hasPrevious())
                .availableGenres(getAvailableGenres())
                .availableLanguages(getAvailableLanguages())
                .availableCities(getAvailableCities())
                .appliedFilters(buildAppliedFilters(request))
                .build();
    }

    /**
     * Get movies by location (city)
     */
    public MovieSearchResponse getMoviesByLocation(String city) {
        log.info("Getting movies for city: {}", city);

        MovieSearchRequest request = MovieSearchRequest.builder()
                .city(city)
                .isActive(true)
                .page(0)
                .size(20)
                .sortBy("title")
                .sortOrder("ASC")
                .build();

        return searchMovies(request);
    }

    /**
     * Get movies by theater
     */
    public MovieSearchResponse getMoviesByTheater(Long theaterId) {
        log.info("Getting movies for theater: {}", theaterId);

        MovieSearchRequest request = MovieSearchRequest.builder()
                .theaterId(theaterId)
                .isActive(true)
                .page(0)
                .size(20)
                .sortBy("title")
                .sortOrder("ASC")
                .build();

        return searchMovies(request);
    }

    /**
     * Get featured movies
     */
    public MovieSearchResponse getFeaturedMovies() {
        log.info("Getting featured movies");

        MovieSearchRequest request = MovieSearchRequest.builder()
                .isFeatured(true)
                .isActive(true)
                .page(0)
                .size(10)
                .sortBy("releaseDate")
                .sortOrder("DESC")
                .build();

        return searchMovies(request);
    }

    /**
     * Get currently showing movies
     */
    public MovieSearchResponse getCurrentlyShowingMovies() {
        log.info("Getting currently showing movies");

        MovieSearchRequest request = MovieSearchRequest.builder()
                .isActive(true)
                .releaseDateFrom(LocalDateTime.now().minusDays(30))
                .releaseDateTo(LocalDateTime.now().plusDays(30))
                .page(0)
                .size(20)
                .sortBy("releaseDate")
                .sortOrder("DESC")
                .build();

        return searchMovies(request);
    }

    /**
     * Build movie specification for filtering
     */
    private Specification<Movie> buildMovieSpecification(MovieSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Title filter
            if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + request.getTitle().toLowerCase() + "%"
                ));
            }

            // Genre filter
            if (request.getGenres() != null && !request.getGenres().isEmpty()) {
                predicates.add(root.get("genre").in(request.getGenres()));
            }

            // Rating filter
            if (request.getRatings() != null && !request.getRatings().isEmpty()) {
                predicates.add(root.get("rating").in(request.getRatings()));
            }

            // Language filter
            if (request.getLanguages() != null && !request.getLanguages().isEmpty()) {
                predicates.add(root.get("language").in(request.getLanguages()));
            }

            // Director filter
            if (request.getDirector() != null && !request.getDirector().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("director")),
                        "%" + request.getDirector().toLowerCase() + "%"
                ));
            }

            // Cast filter
            if (request.getCast() != null && !request.getCast().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("cast")),
                        "%" + request.getCast().toLowerCase() + "%"
                ));
            }

            // Release date filters
            if (request.getReleaseDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("releaseDate"), request.getReleaseDateFrom()
                ));
            }

            if (request.getReleaseDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("releaseDate"), request.getReleaseDateTo()
                ));
            }

            // Active filter
            if (request.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), request.getIsActive()));
            }

            // Featured filter
            if (request.getIsFeatured() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isFeatured"), request.getIsFeatured()));
            }

            // City filter (through shows)
            if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
                // This would require a join with shows and theaters
                // For now, we'll implement a simpler approach
            }

            // Theater filter (through shows)
            if (request.getTheaterId() != null) {
                // This would require a join with shows
                // For now, we'll implement a simpler approach
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Build pageable for pagination and sorting
     */
    private Pageable buildPageable(MovieSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;

        Sort sort = buildSort(request);
        return PageRequest.of(page, size, sort);
    }

    /**
     * Build sort specification
     */
    private Sort buildSort(MovieSearchRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "title";
        String sortOrder = request.getSortOrder() != null ? request.getSortOrder() : "ASC";

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(direction, sortBy);
    }

    /**
     * Get available genres
     */
    private List<String> getAvailableGenres() {
        return movieRepository.findDistinctGenres().stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    /**
     * Get available languages
     */
    private List<String> getAvailableLanguages() {
        return movieRepository.findDistinctLanguages();
    }

    /**
     * Get available cities
     */
    private List<String> getAvailableCities() {
        // This would require a join with theaters
        // For now, return a static list
        return List.of("Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata", "Hyderabad", "Pune", "Ahmedabad");
    }

    /**
     * Build applied filters
     */
    private MovieSearchResponse.SearchFilters buildAppliedFilters(MovieSearchRequest request) {
        return MovieSearchResponse.SearchFilters.builder()
                .title(request.getTitle())
                .genres(request.getGenres() != null ? 
                        request.getGenres().stream().map(Enum::name).collect(Collectors.toList()) : null)
                .ratings(request.getRatings() != null ? 
                        request.getRatings().stream().map(Enum::name).collect(Collectors.toList()) : null)
                .languages(request.getLanguages())
                .director(request.getDirector())
                .city(request.getCity())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();
    }
}

package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.*;
import com.example.MovieTicketBooking.entity.Show;
import com.example.MovieTicketBooking.entity.Screen;
import com.example.MovieTicketBooking.entity.Theater;
import com.example.MovieTicketBooking.repository.ShowRepository;
import com.example.MovieTicketBooking.repository.ScreenRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShowtimeSearchService {

    private final ShowRepository showRepository;
    private final ScreenRepository screenRepository;
    private final TheaterRepository theaterRepository;
    private final ShowService showService;

    /**
     * Search showtimes with advanced filtering
     */
    public ShowtimeSearchResponse searchShowtimes(ShowtimeSearchRequest request) {
        log.info("Searching showtimes with filters: {}", request);

        // Build specification for filtering
        Specification<Show> spec = buildShowSpecification(request);

        // Build pageable for pagination and sorting
        Pageable pageable = buildPageable(request);

        // Execute search
        Page<Show> showPage = showRepository.findAll(spec, pageable);

        // Group shows by theater and screen
        List<ShowtimeSearchResponse.ShowtimeGroup> showtimeGroups = groupShowsByTheaterAndScreen(showPage.getContent());

        // Build response
        return ShowtimeSearchResponse.builder()
                .showtimeGroups(showtimeGroups)
                .totalElements(showPage.getTotalElements())
                .totalPages(showPage.getTotalPages())
                .currentPage(showPage.getNumber())
                .pageSize(showPage.getSize())
                .hasNext(showPage.hasNext())
                .hasPrevious(showPage.hasPrevious())
                .availableScreenTypes(getAvailableScreenTypes())
                .availableLanguages(getAvailableLanguages())
                .availableShowTimes(getAvailableShowTimes())
                .appliedFilters(buildAppliedFilters(request))
                .build();
    }

    /**
     * Get showtimes for a specific movie
     */
    public ShowtimeSearchResponse getShowtimesForMovie(Long movieId, LocalDate showDate) {
        log.info("Getting showtimes for movie: {} on date: {}", movieId, showDate);

        ShowtimeSearchRequest request = ShowtimeSearchRequest.builder()
                .movieId(movieId)
                .showDate(showDate)
                .isActive(true)
                .page(0)
                .size(50)
                .sortBy("showTime")
                .sortOrder("ASC")
                .build();

        return searchShowtimes(request);
    }

    /**
     * Get showtimes for a specific theater
     */
    public ShowtimeSearchResponse getShowtimesForTheater(Long theaterId, LocalDate showDate) {
        log.info("Getting showtimes for theater: {} on date: {}", theaterId, showDate);

        ShowtimeSearchRequest request = ShowtimeSearchRequest.builder()
                .theaterId(theaterId)
                .showDate(showDate)
                .isActive(true)
                .page(0)
                .size(50)
                .sortBy("showTime")
                .sortOrder("ASC")
                .build();

        return searchShowtimes(request);
    }

    /**
     * Get showtimes by city
     */
    public ShowtimeSearchResponse getShowtimesByCity(String city, LocalDate showDate) {
        log.info("Getting showtimes for city: {} on date: {}", city, showDate);

        ShowtimeSearchRequest request = ShowtimeSearchRequest.builder()
                .city(city)
                .showDate(showDate)
                .isActive(true)
                .page(0)
                .size(50)
                .sortBy("showTime")
                .sortOrder("ASC")
                .build();

        return searchShowtimes(request);
    }

    /**
     * Get showtimes for a specific time range
     */
    public ShowtimeSearchResponse getShowtimesByTimeRange(
            LocalDate showDate, 
            LocalTime startTime, 
            LocalTime endTime) {
        log.info("Getting showtimes for date: {} between {} and {}", showDate, startTime, endTime);

        ShowtimeSearchRequest request = ShowtimeSearchRequest.builder()
                .showDate(showDate)
                .showTimeFrom(startTime)
                .showTimeTo(endTime)
                .isActive(true)
                .page(0)
                .size(50)
                .sortBy("showTime")
                .sortOrder("ASC")
                .build();

        return searchShowtimes(request);
    }

    /**
     * Build show specification for filtering
     */
    private Specification<Show> buildShowSpecification(ShowtimeSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Movie filter
            if (request.getMovieId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("movie").get("id"), request.getMovieId()));
            }

            // Theater filter
            if (request.getTheaterId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("screen").get("theater").get("id"), request.getTheaterId()));
            }

            // Screen filter
            if (request.getScreenId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("screen").get("id"), request.getScreenId()));
            }

            // Show date filter
            if (request.getShowDate() != null) {
                LocalDateTime startOfDay = request.getShowDate().atStartOfDay();
                LocalDateTime endOfDay = request.getShowDate().atTime(23, 59, 59);
                predicates.add(criteriaBuilder.between(root.get("showTime"), startOfDay, endOfDay));
            }

            // Show time range filter
            if (request.getShowTimeFrom() != null) {
                LocalDateTime startTime = request.getShowDate() != null ? 
                        request.getShowDate().atTime(request.getShowTimeFrom()) :
                        LocalDate.now().atTime(request.getShowTimeFrom());
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("showTime"), startTime));
            }

            if (request.getShowTimeTo() != null) {
                LocalDateTime endTime = request.getShowDate() != null ? 
                        request.getShowDate().atTime(request.getShowTimeTo()) :
                        LocalDate.now().atTime(request.getShowTimeTo());
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("showTime"), endTime));
            }

            // Screen type filter
            if (request.getScreenTypes() != null && !request.getScreenTypes().isEmpty()) {
                predicates.add(root.get("screen").get("screenType").in(request.getScreenTypes()));
            }

            // Language filter
            if (request.getLanguages() != null && !request.getLanguages().isEmpty()) {
                predicates.add(root.get("movie").get("language").in(request.getLanguages()));
            }

            // Active filter
            if (request.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), request.getIsActive()));
            }

            // City filter
            if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("screen").get("theater").get("city")),
                        "%" + request.getCity().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Build pageable for pagination and sorting
     */
    private Pageable buildPageable(ShowtimeSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;

        Sort sort = buildSort(request);
        return PageRequest.of(page, size, sort);
    }

    /**
     * Build sort specification
     */
    private Sort buildSort(ShowtimeSearchRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "showTime";
        String sortOrder = request.getSortOrder() != null ? request.getSortOrder() : "ASC";

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(direction, sortBy);
    }

    /**
     * Group shows by theater and screen
     */
    private List<ShowtimeSearchResponse.ShowtimeGroup> groupShowsByTheaterAndScreen(List<Show> shows) {
        Map<Long, Map<Long, List<Show>>> groupedShows = shows.stream()
                .collect(Collectors.groupingBy(
                        show -> show.getScreen().getTheater().getId(),
                        Collectors.groupingBy(show -> show.getScreen().getId())
                ));

        List<ShowtimeSearchResponse.ShowtimeGroup> showtimeGroups = new ArrayList<>();

        for (Map.Entry<Long, Map<Long, List<Show>>> theaterEntry : groupedShows.entrySet()) {
            Long theaterId = theaterEntry.getKey();
            Theater theater = theaterRepository.findById(theaterId).orElse(null);
            if (theater == null) continue;

            List<ShowtimeSearchResponse.ScreenShowtimes> screenShowtimes = new ArrayList<>();

            for (Map.Entry<Long, List<Show>> screenEntry : theaterEntry.getValue().entrySet()) {
                Long screenId = screenEntry.getKey();
                List<Show> screenShows = screenEntry.getValue();
                Screen screen = screenRepository.findById(screenId).orElse(null);
                if (screen == null) continue;

                List<ShowResponse> showResponses = screenShows.stream()
                        .map(showService::mapToShowResponse)
                        .collect(Collectors.toList());

                ShowtimeSearchResponse.ScreenShowtimes screenShowtime = ShowtimeSearchResponse.ScreenShowtimes.builder()
                        .screenId(screen.getId())
                        .screenName(screen.getScreenDisplayName())
                        .screenType(screen.getScreenType().getDisplayName())
                        .shows(showResponses)
                        .build();

                screenShowtimes.add(screenShowtime);
            }

            ShowtimeSearchResponse.ShowtimeGroup showtimeGroup = ShowtimeSearchResponse.ShowtimeGroup.builder()
                    .theaterId(theater.getId())
                    .theaterName(theater.getName())
                    .theaterAddress(theater.getAddress())
                    .theaterCity(theater.getCity())
                    .screenShowtimes(screenShowtimes)
                    .build();

            showtimeGroups.add(showtimeGroup);
        }

        return showtimeGroups;
    }

    /**
     * Get available screen types
     */
    private List<String> getAvailableScreenTypes() {
        return screenRepository.findDistinctScreenTypes().stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    /**
     * Get available languages
     */
    private List<String> getAvailableLanguages() {
        return showRepository.findDistinctLanguages();
    }

    /**
     * Get available show times
     */
    private List<String> getAvailableShowTimes() {
        // Return common show time slots
        return List.of(
                "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", 
                "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", 
                "21:00", "22:00", "23:00"
        );
    }

    /**
     * Build applied filters
     */
    private ShowtimeSearchResponse.ShowtimeSearchFilters buildAppliedFilters(ShowtimeSearchRequest request) {
        return ShowtimeSearchResponse.ShowtimeSearchFilters.builder()
                .movieId(request.getMovieId())
                .theaterId(request.getTheaterId())
                .showDate(request.getShowDate() != null ? request.getShowDate().toString() : null)
                .screenTypes(request.getScreenTypes())
                .languages(request.getLanguages())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();
    }
}

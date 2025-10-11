package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.SeatLayoutRequest;
import com.example.MovieTicketBooking.dto.SeatLayoutResponse;
import com.example.MovieTicketBooking.entity.Screen;
import com.example.MovieTicketBooking.entity.Seat;
import com.example.MovieTicketBooking.repository.ScreenRepository;
import com.example.MovieTicketBooking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeatLayoutService {

    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;

    /**
     * Create or update seat layout for a screen
     */
    public SeatLayoutResponse createSeatLayout(SeatLayoutRequest request) {
        log.info("Creating seat layout for screen: {}", request.getScreenId());

        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new RuntimeException("Screen not found"));

        // Clear existing seats
        List<Seat> existingSeats = seatRepository.findByScreenId(request.getScreenId());
        if (!existingSeats.isEmpty()) {
            seatRepository.deleteAll(existingSeats);
            log.info("Deleted {} existing seats for screen: {}", existingSeats.size(), request.getScreenId());
        }

        // Update screen layout information
        screen.setTotalRows(request.getTotalRows());
        screen.setTotalSeatsPerRow(request.getTotalSeatsPerRow());
        screen.calculateTotalSeats();
        screen = screenRepository.save(screen);

        // Create new seats based on layout
        List<Seat> newSeats = createSeatsFromLayout(screen, request);
        seatRepository.saveAll(newSeats);

        log.info("Created {} seats for screen: {}", newSeats.size(), request.getScreenId());

        return buildSeatLayoutResponse(screen, newSeats);
    }

    /**
     * Get seat layout for a screen
     */
    @Transactional(readOnly = true)
    public SeatLayoutResponse getSeatLayout(Long screenId) {
        log.info("Fetching seat layout for screen: {}", screenId);

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found"));

        List<Seat> seats = seatRepository.findByScreenId(screenId);
        return buildSeatLayoutResponse(screen, seats);
    }

    /**
     * Get seat layout with availability status for a show
     */
    @Transactional(readOnly = true)
    public SeatLayoutResponse getSeatLayoutForShow(Long showId) {
        log.info("Fetching seat layout for show: {}", showId);

        // This would need to be implemented with show-specific seat availability
        // For now, return basic layout
        return getSeatLayout(showId); // This needs to be updated to get screen from show
    }

    /**
     * Create seats from layout configuration
     */
    private List<Seat> createSeatsFromLayout(Screen screen, SeatLayoutRequest request) {
        List<Seat> seats = new ArrayList<>();
        int seatCounter = 1;

        // If custom row configuration is provided, use it
        if (request.getSeatRows() != null && !request.getSeatRows().isEmpty()) {
            for (SeatLayoutRequest.SeatRowConfig rowConfig : request.getSeatRows()) {
                for (int seatNum = 1; seatNum <= rowConfig.getSeatsPerRow(); seatNum++) {
                    Seat.SeatType seatType = Seat.SeatType.valueOf(rowConfig.getSeatType());
                    
                    Seat seat = Seat.builder()
                            .screen(screen)
                            .rowNumber(rowConfig.getRowNumber())
                            .seatNumber(seatNum)
                            .seatType(seatType)
                            .isActive(true)
                            .build();
                    
                    seats.add(seat);
                }
            }
        } else {
            // Create uniform layout
            for (int row = 1; row <= request.getTotalRows(); row++) {
                for (int seatNum = 1; seatNum <= request.getTotalSeatsPerRow(); seatNum++) {
                    Seat seat = Seat.builder()
                            .screen(screen)
                            .rowNumber(row)
                            .seatNumber(seatNum)
                            .seatType(Seat.SeatType.STANDARD)
                            .isActive(true)
                            .build();
                    
                    seats.add(seat);
                }
            }
        }

        return seats;
    }

    /**
     * Build seat layout response
     */
    private SeatLayoutResponse buildSeatLayoutResponse(Screen screen, List<Seat> seats) {
        // Group seats by row
        Map<Integer, List<Seat>> seatsByRow = seats.stream()
                .collect(Collectors.groupingBy(Seat::getRowNumber));

        List<SeatLayoutResponse.SeatRowInfo> seatRows = new ArrayList<>();
        
        for (Map.Entry<Integer, List<Seat>> entry : seatsByRow.entrySet()) {
            Integer rowNumber = entry.getKey();
            List<Seat> rowSeats = entry.getValue();
            
            // Sort seats by seat number
            rowSeats.sort(Comparator.comparing(Seat::getSeatNumber));
            
            List<SeatLayoutResponse.SeatInfo> seatInfos = rowSeats.stream()
                    .map(this::mapToSeatInfo)
                    .collect(Collectors.toList());

            SeatLayoutResponse.SeatRowInfo rowInfo = SeatLayoutResponse.SeatRowInfo.builder()
                    .rowNumber(rowNumber)
                    .rowLetter(getRowLetter(rowNumber))
                    .seatsPerRow(rowSeats.size())
                    .seatType(rowSeats.get(0).getSeatType().getDisplayName())
                    .hasAisle(false) // This could be determined from layout config
                    .aislePosition(null)
                    .seats(seatInfos)
                    .build();

            seatRows.add(rowInfo);
        }

        // Sort rows by row number
        seatRows.sort(Comparator.comparing(SeatLayoutResponse.SeatRowInfo::getRowNumber));

        // Count seat types
        Map<String, Integer> seatTypeCounts = seats.stream()
                .collect(Collectors.groupingBy(
                        seat -> seat.getSeatType().getDisplayName(),
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));

        return SeatLayoutResponse.builder()
                .screenId(screen.getId())
                .screenName(screen.getScreenDisplayName())
                .layoutName("Standard Layout") // This could come from screen or request
                .totalRows(screen.getTotalRows())
                .totalSeatsPerRow(screen.getTotalSeatsPerRow())
                .totalSeats(screen.getTotalSeats())
                .seatRows(seatRows)
                .seatTypeCounts(seatTypeCounts)
                .availableSeats(getAvailableSeats(seats))
                .bookedSeats(getBookedSeats(seats))
                .lockedSeats(getLockedSeats(seats))
                .build();
    }

    /**
     * Map seat entity to seat info
     */
    private SeatLayoutResponse.SeatInfo mapToSeatInfo(Seat seat) {
        return SeatLayoutResponse.SeatInfo.builder()
                .seatId(seat.getId())
                .seatIdentifier(seat.getSeatIdentifier())
                .seatType(seat.getSeatType().getDisplayName())
                .status("AVAILABLE") // This would need to be determined from booking status
                .isActive(seat.getIsActive())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .build();
    }

    /**
     * Get row letter from row number
     */
    private String getRowLetter(int rowNumber) {
        return String.valueOf((char) ('A' + rowNumber - 1));
    }

    /**
     * Get available seats (placeholder implementation)
     */
    private List<String> getAvailableSeats(List<Seat> seats) {
        return seats.stream()
                .filter(Seat::getIsActive)
                .map(Seat::getSeatIdentifier)
                .collect(Collectors.toList());
    }

    /**
     * Get booked seats (placeholder implementation)
     */
    private List<String> getBookedSeats(List<Seat> seats) {
        // This would need to check booking status
        return new ArrayList<>();
    }

    /**
     * Get locked seats (placeholder implementation)
     */
    private List<String> getLockedSeats(List<Seat> seats) {
        // This would need to check lock status
        return new ArrayList<>();
    }
}

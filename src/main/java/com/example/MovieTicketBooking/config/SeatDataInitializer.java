package com.example.MovieTicketBooking.config;

import com.example.MovieTicketBooking.entity.Seat;
import com.example.MovieTicketBooking.entity.Screen;
import com.example.MovieTicketBooking.repository.SeatRepository;
import com.example.MovieTicketBooking.repository.ScreenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SeatDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeatDataInitializer.class);
    
    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking seat data initialization...");
        
        // Get all screens
        List<Screen> screens = screenRepository.findAll();
        log.info("Found {} screens in database", screens.size());
        
        for (Screen screen : screens) {
            List<Seat> existingSeats = seatRepository.findByScreenId(screen.getId());
            log.info("Screen {} has {} existing seats", screen.getScreenDisplayName(), existingSeats.size());
            
            // If no seats exist for this screen, create them with proper types
            if (existingSeats.isEmpty()) {
                log.info("Creating seats for screen: {} ({} rows, {} seats per row)", 
                        screen.getScreenDisplayName(), screen.getTotalRows(), screen.getTotalSeatsPerRow());
                
                List<Seat> newSeats = createSeatsWithTypes(screen);
                seatRepository.saveAll(newSeats);
                
                log.info("Created {} seats for screen: {}", newSeats.size(), screen.getScreenDisplayName());
                
                // Log seat type distribution
                Map<String, Long> seatTypeCounts = newSeats.stream()
                        .collect(java.util.stream.Collectors.groupingBy(
                                seat -> seat.getSeatType().name(),
                                java.util.stream.Collectors.counting()
                        ));
                log.info("Seat type distribution for screen {}: {}", screen.getScreenDisplayName(), seatTypeCounts);
            } else {
                // Check if seats have proper types (not all STANDARD)
                boolean hasVariedTypes = existingSeats.stream()
                        .anyMatch(seat -> seat.getSeatType() != Seat.SeatType.STANDARD);
                
                log.info("Screen {} has varied seat types: {}", screen.getScreenDisplayName(), hasVariedTypes);
                
                if (!hasVariedTypes) {
                    log.info("Updating seat types for screen: {}", screen.getScreenDisplayName());
                    updateSeatTypes(existingSeats);
                    seatRepository.saveAll(existingSeats);
                    log.info("Updated seat types for {} seats in screen: {}", 
                            existingSeats.size(), screen.getScreenDisplayName());
                    
                    // Log updated seat type distribution
                    Map<String, Long> seatTypeCounts = existingSeats.stream()
                            .collect(java.util.stream.Collectors.groupingBy(
                                    seat -> seat.getSeatType().name(),
                                    java.util.stream.Collectors.counting()
                            ));
                    log.info("Updated seat type distribution for screen {}: {}", screen.getScreenDisplayName(), seatTypeCounts);
                } else {
                    // Log current seat type distribution
                    Map<String, Long> seatTypeCounts = existingSeats.stream()
                            .collect(java.util.stream.Collectors.groupingBy(
                                    seat -> seat.getSeatType().name(),
                                    java.util.stream.Collectors.counting()
                            ));
                    log.info("Current seat type distribution for screen {}: {}", screen.getScreenDisplayName(), seatTypeCounts);
                }
            }
        }
    }
    
    private List<Seat> createSeatsWithTypes(Screen screen) {
        List<Seat> seats = new ArrayList<>();
        
        for (int row = 1; row <= screen.getTotalRows(); row++) {
            for (int seatNum = 1; seatNum <= screen.getTotalSeatsPerRow(); seatNum++) {
                Seat.SeatType seatType = determineSeatType(row, screen.getTotalRows());
                
                Seat seat = Seat.builder()
                        .screen(screen)
                        .rowNumber(row)
                        .seatNumber(seatNum)
                        .seatType(seatType)
                        .isActive(true)
                        .build();
                
                seats.add(seat);
            }
        }
        
        return seats;
    }
    
    private void updateSeatTypes(List<Seat> seats) {
        // Group seats by row to determine types
        int maxRow = seats.stream().mapToInt(Seat::getRowNumber).max().orElse(1);
        
        for (Seat seat : seats) {
            seat.setSeatType(determineSeatType(seat.getRowNumber(), maxRow));
        }
    }
    
    private Seat.SeatType determineSeatType(int rowNumber, int totalRows) {
        // Front rows (1-2) = VIP
        if (rowNumber <= 2) {
            return Seat.SeatType.VIP;
        }
        // Middle rows (3-4) = PREMIUM  
        else if (rowNumber <= 4) {
            return Seat.SeatType.PREMIUM;
        }
        // Back rows = STANDARD
        else {
            return Seat.SeatType.STANDARD;
        }
    }
}

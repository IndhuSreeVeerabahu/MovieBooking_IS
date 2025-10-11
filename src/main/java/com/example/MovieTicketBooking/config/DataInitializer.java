package com.example.MovieTicketBooking.config;

import com.example.MovieTicketBooking.entity.Movie;
import com.example.MovieTicketBooking.entity.Theater;
import com.example.MovieTicketBooking.entity.Screen;
import com.example.MovieTicketBooking.entity.Seat;
import com.example.MovieTicketBooking.entity.Show;
import com.example.MovieTicketBooking.entity.User;
import com.example.MovieTicketBooking.repository.MovieRepository;
import com.example.MovieTicketBooking.repository.TheaterRepository;
import com.example.MovieTicketBooking.repository.ScreenRepository;
import com.example.MovieTicketBooking.repository.SeatRepository;
import com.example.MovieTicketBooking.repository.ShowRepository;
import com.example.MovieTicketBooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Check if data already exists
        if (movieRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        // Create users
        createUsers();
        
        // Create theaters
        createTheaters();
        
        // Create movies
        createMovies();
        
        // Create screens
        createScreens();
        
        // Create seats
        createSeats();
        
        // Create shows
        createShows();
        
        log.info("Data initialization completed successfully!");
    }

    private void createUsers() {
        log.info("Creating users...");
        
        User admin = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@moviehub.com")
                .password(passwordEncoder.encode("admin123"))
                .phoneNumber("+91-9876543210")
                .role(User.Role.ADMIN)
                .isEnabled(true)
                .build();
        userRepository.save(admin);

        User user = User.builder()
                .firstName("Rajesh")
                .lastName("Kumar")
                .email("rajesh.kumar@gmail.com")
                .password(passwordEncoder.encode("user123"))
                .phoneNumber("+91-9876543211")
                .role(User.Role.USER)
                .isEnabled(true)
                .build();
        userRepository.save(user);
        
        log.info("Users created successfully");
    }

    private void createTheaters() {
        log.info("Creating theaters...");
        
        Theater theater1 = Theater.builder()
                .name("PVR Cinemas Phoenix MarketCity")
                .address("142, Velachery Main Road, Velachery")
                .city("Chennai")
                .state("Tamil Nadu")
                .pincode("600042")
                .phoneNumber("+91-44-12345678")
                .amenities("IMAX, Dolby Atmos, Recliner Seats, Food Court")
                .isActive(true)
                .build();
        theaterRepository.save(theater1);

        Theater theater2 = Theater.builder()
                .name("INOX Megaplex Express Avenue")
                .address("Express Avenue Mall, White's Road, Royapettah")
                .city("Chennai")
                .state("Tamil Nadu")
                .pincode("600014")
                .phoneNumber("+91-44-87654321")
                .amenities("4DX, IMAX, Premium Lounges, Parking")
                .isActive(true)
                .build();
        theaterRepository.save(theater2);

        Theater theater3 = Theater.builder()
                .name("Cinepolis Forum Mall")
                .address("Forum Vijaya Mall, 183, Arcot Road, Vadapalani")
                .city("Chennai")
                .state("Tamil Nadu")
                .pincode("600026")
                .phoneNumber("+91-44-98765432")
                .amenities("Standard Screens, Concessions, Gaming Zone")
                .isActive(true)
                .build();
        theaterRepository.save(theater3);

        Theater theater4 = Theater.builder()
                .name("Sathyam Cinemas")
                .address("8, Thiruvika Road, Royapettah")
                .city("Chennai")
                .state("Tamil Nadu")
                .pincode("600014")
                .phoneNumber("+91-44-23456789")
                .amenities("Luxury Seating, Bar Service, Valet Parking")
                .isActive(true)
                .build();
        theaterRepository.save(theater4);
        
        log.info("Theaters created successfully");
    }

    private void createMovies() {
        log.info("Creating movies...");
        
        // Create sample movies with current dates
        Movie movie1 = Movie.builder()
                .title("Avengers: Endgame")
                .description("After the devastating events of Infinity War, the universe is in ruins. With the help of remaining allies, the Avengers assemble once more.")
                .genre(Movie.Genre.ACTION)
                .durationMinutes(181)
                .rating(Movie.Rating.PG_13)
                .posterUrl("https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg")
                .releaseDate(LocalDateTime.of(2025, 9, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 11, 15, 0, 0))
                .director("Anthony Russo, Joe Russo")
                .cast("Robert Downey Jr., Chris Evans, Mark Ruffalo")
                .language("English")
                .isActive(true)
                .isFeatured(true)
                .build();
        movieRepository.save(movie1);

        Movie movie2 = Movie.builder()
                .title("The Dark Knight")
                .description("When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological tests.")
                .genre(Movie.Genre.ACTION)
                .durationMinutes(152)
                .rating(Movie.Rating.PG_13)
                .posterUrl("https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg")
                .releaseDate(LocalDateTime.of(2025, 9, 15, 0, 0))
                .endDate(LocalDateTime.of(2025, 12, 1, 0, 0))
                .director("Christopher Nolan")
                .cast("Christian Bale, Heath Ledger, Aaron Eckhart")
                .language("English")
                .isActive(true)
                .isFeatured(false)
                .build();
        movieRepository.save(movie2);

        Movie movie3 = Movie.builder()
                .title("Inception")
                .description("A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.")
                .genre(Movie.Genre.SCI_FI)
                .durationMinutes(148)
                .rating(Movie.Rating.PG_13)
                .posterUrl("https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg")
                .releaseDate(LocalDateTime.of(2025, 10, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 0, 0))
                .director("Christopher Nolan")
                .cast("Leonardo DiCaprio, Marion Cotillard, Tom Hardy")
                .language("English")
                .isActive(true)
                .isFeatured(true)
                .build();
        movieRepository.save(movie3);

        Movie movie4 = Movie.builder()
                .title("Spider-Man: No Way Home")
                .description("With Spider-Man's identity now revealed, Peter asks Doctor Strange for help. When a spell goes wrong, dangerous foes from other worlds start to appear.")
                .genre(Movie.Genre.ACTION)
                .durationMinutes(148)
                .rating(Movie.Rating.PG_13)
                .posterUrl("https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg")
                .releaseDate(LocalDateTime.of(2025, 11, 1, 0, 0))
                .endDate(LocalDateTime.of(2026, 1, 15, 0, 0))
                .director("Jon Watts")
                .cast("Tom Holland, Zendaya, Benedict Cumberbatch")
                .language("English")
                .isActive(true)
                .isFeatured(true)
                .build();
        movieRepository.save(movie4);

        Movie movie5 = Movie.builder()
                .title("Top Gun: Maverick")
                .description("After thirty years, Maverick is still pushing the envelope as a top naval aviator, but must confront ghosts of his past when he leads TOP GUN's elite graduates.")
                .genre(Movie.Genre.ACTION)
                .durationMinutes(131)
                .rating(Movie.Rating.PG_13)
                .posterUrl("https://image.tmdb.org/t/p/w500/62HCnUTziyWcpDaBO2i1DX17ljH.jpg")
                .releaseDate(LocalDateTime.of(2025, 12, 1, 0, 0))
                .endDate(LocalDateTime.of(2026, 2, 15, 0, 0))
                .director("Joseph Kosinski")
                .cast("Tom Cruise, Miles Teller, Jennifer Connelly")
                .language("English")
                .isActive(true)
                .isFeatured(true)
                .build();
        movieRepository.save(movie5);
        
        log.info("Movies created successfully");
    }

    private void createScreens() {
        log.info("Creating screens...");
        
        var theaters = theaterRepository.findAll();
        
        // Screen 1: IMAX (200 seats) - 10 rows x 20 seats
        Screen screen1 = Screen.builder()
                .theater(theaters.get(0))
                .screenNumber(1)
                .screenName("IMAX Screen 1")
                .totalRows(10)
                .totalSeatsPerRow(20)
                .totalSeats(200)
                .screenType(Screen.ScreenType.IMAX)
                .isActive(true)
                .build();
        screenRepository.save(screen1);

        // Screen 2: Standard (150 seats) - 10 rows x 15 seats
        Screen screen2 = Screen.builder()
                .theater(theaters.get(0))
                .screenNumber(2)
                .screenName("Standard Screen 2")
                .totalRows(10)
                .totalSeatsPerRow(15)
                .totalSeats(150)
                .screenType(Screen.ScreenType.STANDARD)
                .isActive(true)
                .build();
        screenRepository.save(screen2);

        // Screen 3: Standard (100 seats) - 8 rows x 12-13 seats
        Screen screen3 = Screen.builder()
                .theater(theaters.get(0))
                .screenNumber(3)
                .screenName("Standard Screen 3")
                .totalRows(8)
                .totalSeatsPerRow(13)
                .totalSeats(104)
                .screenType(Screen.ScreenType.STANDARD)
                .isActive(true)
                .build();
        screenRepository.save(screen3);

        // Screen 4: Premium (180 seats) - 12 rows x 15 seats
        Screen screen4 = Screen.builder()
                .theater(theaters.get(1))
                .screenNumber(1)
                .screenName("Premium Screen 1")
                .totalRows(12)
                .totalSeatsPerRow(15)
                .totalSeats(180)
                .screenType(Screen.ScreenType.PREMIUM)
                .isActive(true)
                .build();
        screenRepository.save(screen4);

        // Screen 5: Standard (120 seats) - 8 rows x 15 seats
        Screen screen5 = Screen.builder()
                .theater(theaters.get(1))
                .screenNumber(2)
                .screenName("Standard Screen 2")
                .totalRows(8)
                .totalSeatsPerRow(15)
                .totalSeats(120)
                .screenType(Screen.ScreenType.STANDARD)
                .isActive(true)
                .build();
        screenRepository.save(screen5);
        
        log.info("Screens created successfully");
    }

    private void createSeats() {
        log.info("Creating seats...");
        
        var screens = screenRepository.findAll();
        
        // Create seats for first screen (IMAX - 200 seats)
        Screen screen1 = screens.get(0);
        for (int row = 1; row <= 10; row++) {
            for (int seat = 1; seat <= 20; seat++) {
                Seat.SeatType seatType = row <= 2 ? Seat.SeatType.VIP : 
                                       row <= 5 ? Seat.SeatType.PREMIUM : Seat.SeatType.STANDARD;
                
                Seat seatEntity = Seat.builder()
                        .screen(screen1)
                        .rowNumber(row)
                        .seatNumber(seat)
                        .seatType(seatType)
                        .isActive(true)
                        .build();
                seatRepository.save(seatEntity);
            }
        }

        // Create seats for second screen (Standard - 150 seats)
        Screen screen2 = screens.get(1);
        for (int row = 1; row <= 10; row++) {
            for (int seat = 1; seat <= 15; seat++) {
                Seat.SeatType seatType = row <= 2 ? Seat.SeatType.PREMIUM : Seat.SeatType.STANDARD;
                
                Seat seatEntity = Seat.builder()
                        .screen(screen2)
                        .rowNumber(row)
                        .seatNumber(seat)
                        .seatType(seatType)
                        .isActive(true)
                        .build();
                seatRepository.save(seatEntity);
            }
        }
        
        log.info("Seats created successfully");
    }

    private void createShows() {
        log.info("Creating shows...");
        
        var movies = movieRepository.findAll();
        var screens = screenRepository.findAll();
        
        // Create shows for today
        LocalDate today = LocalDate.now();
        
        // Avengers: Endgame shows (Indian pricing)
        Show show1 = Show.builder()
                .movie(movies.get(0))
                .theater(screens.get(0).getTheater())
                .screen(screens.get(0))
                .showDate(today)
                .showTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(13, 1))
                .basePrice(new BigDecimal("250.00"))
                .premiumPrice(new BigDecimal("350.00"))
                .vipPrice(new BigDecimal("500.00"))
                .showStatus(Show.ShowStatus.ACTIVE)
                .isActive(true)
                .build();
        showRepository.save(show1);

        Show show2 = Show.builder()
                .movie(movies.get(0))
                .theater(screens.get(0).getTheater())
                .screen(screens.get(0))
                .showDate(today)
                .showTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(17, 1))
                .basePrice(new BigDecimal("300.00"))
                .premiumPrice(new BigDecimal("400.00"))
                .vipPrice(new BigDecimal("550.00"))
                .showStatus(Show.ShowStatus.ACTIVE)
                .isActive(true)
                .build();
        showRepository.save(show2);

        // The Dark Knight shows (Indian pricing)
        Show show3 = Show.builder()
                .movie(movies.get(1))
                .theater(screens.get(1).getTheater())
                .screen(screens.get(1))
                .showDate(today)
                .showTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(13, 32))
                .basePrice(new BigDecimal("200.00"))
                .premiumPrice(new BigDecimal("300.00"))
                .vipPrice(new BigDecimal("450.00"))
                .showStatus(Show.ShowStatus.ACTIVE)
                .isActive(true)
                .build();
        showRepository.save(show3);
        
        log.info("Shows created successfully");
    }
}

-- Script to verify that demo data has been loaded correctly
USE movie_ticket_booking;

-- Check movies
SELECT 'Movies Count:' as Check_Type, COUNT(*) as Count FROM movies;
SELECT 'Sample Movies:' as Check_Type, title, genre, rating FROM movies LIMIT 5;

-- Check theaters
SELECT 'Theaters Count:' as Check_Type, COUNT(*) as Count FROM theaters;
SELECT 'Sample Theaters:' as Check_Type, name, city FROM theaters;

-- Check screens
SELECT 'Screens Count:' as Check_Type, COUNT(*) as Count FROM screens;
SELECT 'Sample Screens:' as Check_Type, screen_number, capacity, screen_type FROM screens LIMIT 5;

-- Check seats
SELECT 'Seats Count:' as Check_Type, COUNT(*) as Count FROM seats;
SELECT 'Sample Seats:' as Check_Type, row_number, seat_number, seat_type FROM seats LIMIT 5;

-- Check shows
SELECT 'Shows Count:' as Check_Type, COUNT(*) as Count FROM shows;
SELECT 'Sample Shows:' as Check_Type, show_date, show_time, base_price FROM shows LIMIT 5;

-- Check users
SELECT 'Users Count:' as Check_Type, COUNT(*) as Count FROM users;
SELECT 'Sample Users:' as Check_Type, first_name, last_name, email, role FROM users;

-- Check bookings (if any)
SELECT 'Bookings Count:' as Check_Type, COUNT(*) as Count FROM bookings;

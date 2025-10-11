-- =============================================
-- DEBUG SEAT LAYOUT ISSUE
-- Run this to check what's happening
-- =============================================

USE movie_ticket_booking;

-- Check if seats exist
SELECT 'Total Seats:' as info, COUNT(*) as count FROM seats;

-- Check seats by screen
SELECT 'Seats by Screen:' as info, screen_id, COUNT(*) as count FROM seats GROUP BY screen_id ORDER BY screen_id;

-- Check what screens exist
SELECT 'Available Screens:' as info, id, screen_number, screen_name, theater_id FROM screens ORDER BY id LIMIT 10;

-- Check what shows exist
SELECT 'Available Shows:' as info, id, movie_id, theater_id, screen_id, show_date, show_time FROM shows ORDER BY id LIMIT 10;

-- Check if seats exist for the first show's screen
SELECT 'Seats for First Show Screen:' as info, 
       s.id as seat_id, 
       s.screen_id, 
       s.row_number, 
       s.seat_number, 
       s.seat_type,
       CONCAT(CHAR(64 + s.row_number), s.seat_number) as seat_identifier
FROM seats s 
WHERE s.screen_id = (SELECT MIN(screen_id) FROM shows)
ORDER BY s.row_number, s.seat_number
LIMIT 20;

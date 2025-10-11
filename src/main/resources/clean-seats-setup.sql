-- =============================================
-- CLEAN SEATS SETUP - LIKE REAL MOVIE BOOKING WEBSITES
-- This creates seats automatically for ALL screens
-- =============================================

USE movie_ticket_booking;

-- =============================================
-- 1. DROP AND RECREATE SEATS TABLE (CLEAN START)
-- =============================================

-- Drop existing tables
DROP TABLE IF EXISTS booking_seats;
DROP TABLE IF EXISTS seats;

-- Create clean seats table
CREATE TABLE seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    screen_id BIGINT NOT NULL,
    row_number INT NOT NULL,
    seat_number INT NOT NULL,
    seat_type ENUM('STANDARD', 'PREMIUM', 'VIP') NOT NULL DEFAULT 'STANDARD',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (screen_id) REFERENCES screens(id) ON DELETE CASCADE,
    UNIQUE KEY unique_seat_per_screen (screen_id, row_number, seat_number)
);

-- Create booking_seats table
CREATE TABLE booking_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    seat_status ENUM('BOOKED', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'BOOKED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE
);

-- =============================================
-- 2. CREATE SEATS FOR ALL SCREENS AUTOMATICALLY
-- =============================================

-- This will create seats for EVERY screen in your database
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 
    s.id as screen_id,
    r.row_num,
    seat.seat_num,
    CASE 
        WHEN r.row_num <= 2 THEN 'VIP'
        WHEN r.row_num <= 4 THEN 'PREMIUM'
        ELSE 'STANDARD'
    END as seat_type,
    true,
    NOW(),
    NOW()
FROM screens s
CROSS JOIN (
    SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 
    UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
) r
CROSS JOIN (
    SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
    UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
    UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
) seat
WHERE r.row_num <= s.total_rows 
  AND seat.seat_num <= s.total_seats_per_row;

-- =============================================
-- 3. VERIFY RESULTS
-- =============================================

-- Check total seats created
SELECT 'TOTAL SEATS CREATED:' as info, COUNT(*) as count FROM seats;

-- Check seats by screen
SELECT 'SEATS BY SCREEN:' as info, 
       s.screen_id, 
       sc.screen_name, 
       sc.total_rows, 
       sc.total_seats_per_row,
       COUNT(*) as actual_seats
FROM seats s
JOIN screens sc ON s.screen_id = sc.id
GROUP BY s.screen_id, sc.screen_name, sc.total_rows, sc.total_seats_per_row
ORDER BY s.screen_id;

-- Check seats by type
SELECT 'SEATS BY TYPE:' as info, seat_type, COUNT(*) as count FROM seats GROUP BY seat_type;

-- Show sample seats for first screen
SELECT 'SAMPLE SEATS (First Screen):' as info, 
       id, screen_id, row_number, seat_number, seat_type,
       CONCAT(CHAR(64 + row_number), seat_number) as seat_label
FROM seats 
WHERE screen_id = (SELECT MIN(id) FROM screens)
ORDER BY row_number, seat_number 
LIMIT 20;

-- =============================================
-- 4. SUCCESS MESSAGE
-- =============================================
SELECT '🎬 SUCCESS! All seats created automatically for all screens!' as message;

-- =============================================
-- CREATE SEATS TABLE
-- =============================================

USE movie_ticket_booking;

-- Create seats table if it doesn't exist
CREATE TABLE IF NOT EXISTS seats (
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

-- Clear existing seats data
DELETE FROM seats;

-- =============================================
-- GENERATE SEATS FOR ALL SCREENS
-- =============================================

-- Screen 1 (IMAX, 240 seats) - PVR Phoenix MarketCity
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 1, row_num, seat_num, 
       CASE 
           WHEN row_num <= 2 THEN 'VIP'
           WHEN row_num <= 4 THEN 'PREMIUM'
           ELSE 'STANDARD'
       END,
       true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
    ) seats
) seat_combinations;

-- Screen 2 (Standard, 180 seats) - PVR Phoenix MarketCity
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 2, row_num, seat_num, 
       CASE 
           WHEN row_num <= 2 THEN 'PREMIUM'
           ELSE 'STANDARD'
       END,
       true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18
    ) seats
) seat_combinations;

-- Screen 3 (Standard, 160 seats) - PVR Phoenix MarketCity
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 3, row_num, seat_num, 
       CASE 
           WHEN row_num <= 2 THEN 'PREMIUM'
           ELSE 'STANDARD'
       END,
       true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16
    ) seats
) seat_combinations;

-- Screen 4 (Premium, 96 seats) - PVR Phoenix MarketCity
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 4, row_num, seat_num, 
       CASE 
           WHEN row_num <= 2 THEN 'VIP'
           ELSE 'PREMIUM'
       END,
       true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12
    ) seats
) seat_combinations;

-- Screen 5 (VIP, 60 seats) - PVR Phoenix MarketCity
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 5, row_num, seat_num, 'VIP', true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    ) seats
) seat_combinations;

-- Screen 6 (Standard, 120 seats) - PVR Phoenix MarketCity
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 6, row_num, seat_num, 
       CASE 
           WHEN row_num <= 2 THEN 'PREMIUM'
           ELSE 'STANDARD'
       END,
       true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
    ) seats
) seat_combinations;

-- Add seats for more screens (simplified for key screens)
-- Screen 7 (Standard, 180 seats) - INOX Express Avenue
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 7, row_num, seat_num, 
       CASE 
           WHEN row_num <= 2 THEN 'PREMIUM'
           ELSE 'STANDARD'
       END,
       true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18
    ) seats
) seat_combinations;

-- Screen 8 (IMAX, 240 seats) - INOX Express Avenue
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 8, row_num, seat_num, 
       CASE 
           WHEN row_num <= 2 THEN 'VIP'
           WHEN row_num <= 4 THEN 'PREMIUM'
           ELSE 'STANDARD'
       END,
       true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
    ) seats
) seat_combinations;

-- Screen 13 (Luxury, 96 seats) - Sathyam Cinemas
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 13, row_num, seat_num, 
       CASE 
           WHEN row_num <= 2 THEN 'VIP'
           ELSE 'PREMIUM'
       END,
       true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12
    ) seats
) seat_combinations;

-- Screen 19 (Standard, 180 seats) - PVR VR Mall
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 19, row_num, seat_num, 
       CASE 
           WHEN row_num <= 2 THEN 'PREMIUM'
           ELSE 'STANDARD'
       END,
       true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18
    ) seats
) seat_combinations;

-- Screen 31 (IMAX, 240 seats) - Mayajaal Multiplex
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at)
SELECT 31, row_num, seat_num, 
       CASE 
           WHEN row_num <= 2 THEN 'VIP'
           WHEN row_num <= 4 THEN 'PREMIUM'
           ELSE 'STANDARD'
       END,
       true, NOW(), NOW()
FROM (
    SELECT row_num, seat_num
    FROM (
        SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12
    ) rows
    CROSS JOIN (
        SELECT 1 as seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
    ) seats
) seat_combinations;

-- =============================================
-- VERIFY SEATS CREATION
-- =============================================

-- Check total seats created
SELECT COUNT(*) as total_seats FROM seats;

-- Check seats by screen
SELECT screen_id, COUNT(*) as seat_count FROM seats GROUP BY screen_id ORDER BY screen_id;

-- Check seats by type
SELECT seat_type, COUNT(*) as seat_count FROM seats GROUP BY seat_type;

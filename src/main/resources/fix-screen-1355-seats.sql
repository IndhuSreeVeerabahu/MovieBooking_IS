-- =============================================
-- FIX SEATS FOR SCREEN 1355
-- This will create seats for the specific screen that's missing seats
-- =============================================

USE movie_ticket_booking;

-- Create seats for Screen 1355 (8 rows x 15 seats = 120 seats)
INSERT INTO seats (screen_id, row_number, seat_number, seat_type, is_active, created_at, updated_at) VALUES
-- Row 1 (VIP)
(1355, 1, 1, 'VIP', true, NOW(), NOW()),
(1355, 1, 2, 'VIP', true, NOW(), NOW()),
(1355, 1, 3, 'VIP', true, NOW(), NOW()),
(1355, 1, 4, 'VIP', true, NOW(), NOW()),
(1355, 1, 5, 'VIP', true, NOW(), NOW()),
(1355, 1, 6, 'VIP', true, NOW(), NOW()),
(1355, 1, 7, 'VIP', true, NOW(), NOW()),
(1355, 1, 8, 'VIP', true, NOW(), NOW()),
(1355, 1, 9, 'VIP', true, NOW(), NOW()),
(1355, 1, 10, 'VIP', true, NOW(), NOW()),
(1355, 1, 11, 'VIP', true, NOW(), NOW()),
(1355, 1, 12, 'VIP', true, NOW(), NOW()),
(1355, 1, 13, 'VIP', true, NOW(), NOW()),
(1355, 1, 14, 'VIP', true, NOW(), NOW()),
(1355, 1, 15, 'VIP', true, NOW(), NOW()),

-- Row 2 (VIP)
(1355, 2, 1, 'VIP', true, NOW(), NOW()),
(1355, 2, 2, 'VIP', true, NOW(), NOW()),
(1355, 2, 3, 'VIP', true, NOW(), NOW()),
(1355, 2, 4, 'VIP', true, NOW(), NOW()),
(1355, 2, 5, 'VIP', true, NOW(), NOW()),
(1355, 2, 6, 'VIP', true, NOW(), NOW()),
(1355, 2, 7, 'VIP', true, NOW(), NOW()),
(1355, 2, 8, 'VIP', true, NOW(), NOW()),
(1355, 2, 9, 'VIP', true, NOW(), NOW()),
(1355, 2, 10, 'VIP', true, NOW(), NOW()),
(1355, 2, 11, 'VIP', true, NOW(), NOW()),
(1355, 2, 12, 'VIP', true, NOW(), NOW()),
(1355, 2, 13, 'VIP', true, NOW(), NOW()),
(1355, 2, 14, 'VIP', true, NOW(), NOW()),
(1355, 2, 15, 'VIP', true, NOW(), NOW()),

-- Row 3 (PREMIUM)
(1355, 3, 1, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 2, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 3, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 4, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 5, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 6, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 7, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 8, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 9, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 10, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 11, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 12, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 13, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 14, 'PREMIUM', true, NOW(), NOW()),
(1355, 3, 15, 'PREMIUM', true, NOW(), NOW()),

-- Row 4 (PREMIUM)
(1355, 4, 1, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 2, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 3, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 4, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 5, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 6, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 7, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 8, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 9, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 10, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 11, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 12, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 13, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 14, 'PREMIUM', true, NOW(), NOW()),
(1355, 4, 15, 'PREMIUM', true, NOW(), NOW()),

-- Row 5 (STANDARD)
(1355, 5, 1, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 2, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 3, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 4, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 5, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 6, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 7, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 8, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 9, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 10, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 11, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 12, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 13, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 14, 'STANDARD', true, NOW(), NOW()),
(1355, 5, 15, 'STANDARD', true, NOW(), NOW()),

-- Row 6 (STANDARD)
(1355, 6, 1, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 2, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 3, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 4, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 5, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 6, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 7, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 8, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 9, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 10, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 11, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 12, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 13, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 14, 'STANDARD', true, NOW(), NOW()),
(1355, 6, 15, 'STANDARD', true, NOW(), NOW()),

-- Row 7 (STANDARD)
(1355, 7, 1, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 2, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 3, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 4, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 5, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 6, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 7, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 8, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 9, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 10, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 11, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 12, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 13, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 14, 'STANDARD', true, NOW(), NOW()),
(1355, 7, 15, 'STANDARD', true, NOW(), NOW()),

-- Row 8 (STANDARD)
(1355, 8, 1, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 2, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 3, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 4, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 5, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 6, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 7, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 8, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 9, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 10, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 11, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 12, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 13, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 14, 'STANDARD', true, NOW(), NOW()),
(1355, 8, 15, 'STANDARD', true, NOW(), NOW());

-- Verify the seats were created
SELECT 'SUCCESS! Seats created for Screen 1355:' as message, COUNT(*) as count FROM seats WHERE screen_id = 1355;
SELECT 'Seats by Type for Screen 1355:' as message, seat_type, COUNT(*) as count FROM seats WHERE screen_id = 1355 GROUP BY seat_type;
SELECT 'Sample Seats for Screen 1355:' as message, id, screen_id, row_number, seat_number, seat_type FROM seats WHERE screen_id = 1355 ORDER BY row_number, seat_number LIMIT 10;

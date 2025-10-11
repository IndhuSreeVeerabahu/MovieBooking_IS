-- Script to fix movie dates to make them currently showing
-- Run this script in your MySQL database to update existing movie dates

USE movie_ticket_booking;

-- Update movie release and end dates to make them currently showing
UPDATE movies SET 
    release_date = '2025-09-01 00:00:00',
    end_date = '2025-11-15 00:00:00'
WHERE title = 'Avengers: Endgame';

UPDATE movies SET 
    release_date = '2025-09-15 00:00:00',
    end_date = '2025-12-01 00:00:00'
WHERE title = 'The Dark Knight';

UPDATE movies SET 
    release_date = '2025-10-01 00:00:00',
    end_date = '2025-12-15 00:00:00'
WHERE title = 'Inception';

UPDATE movies SET 
    release_date = '2025-09-20 00:00:00',
    end_date = '2025-11-30 00:00:00'
WHERE title = 'The Shawshank Redemption';

UPDATE movies SET 
    release_date = '2025-10-15 00:00:00',
    end_date = '2025-12-30 00:00:00'
WHERE title = 'Pulp Fiction';

UPDATE movies SET 
    release_date = '2025-11-01 00:00:00',
    end_date = '2026-01-15 00:00:00'
WHERE title = 'Spider-Man: No Way Home';

UPDATE movies SET 
    release_date = '2025-11-15 00:00:00',
    end_date = '2026-01-30 00:00:00'
WHERE title = 'Dune';

UPDATE movies SET 
    release_date = '2025-12-01 00:00:00',
    end_date = '2026-02-15 00:00:00'
WHERE title = 'Top Gun: Maverick';

UPDATE movies SET 
    release_date = '2025-12-15 00:00:00',
    end_date = '2026-03-01 00:00:00'
WHERE title = 'The Batman';

UPDATE movies SET 
    release_date = '2026-01-01 00:00:00',
    end_date = '2026-03-15 00:00:00'
WHERE title = 'Encanto';

UPDATE movies SET 
    release_date = '2026-01-15 00:00:00',
    end_date = '2026-03-30 00:00:00'
WHERE title = 'No Time to Die';

UPDATE movies SET 
    release_date = '2026-02-01 00:00:00',
    end_date = '2026-04-15 00:00:00'
WHERE title = 'Black Widow';

UPDATE movies SET 
    release_date = '2026-02-15 00:00:00',
    end_date = '2026-05-01 00:00:00'
WHERE title = 'Avatar: The Way of Water';

UPDATE movies SET 
    release_date = '2026-03-01 00:00:00',
    end_date = '2026-05-15 00:00:00'
WHERE title = 'Wakanda Forever';

UPDATE movies SET 
    release_date = '2026-03-15 00:00:00',
    end_date = '2026-06-01 00:00:00'
WHERE title = 'Jurassic World Dominion';

UPDATE movies SET 
    release_date = '2026-04-01 00:00:00',
    end_date = '2026-06-15 00:00:00'
WHERE title = 'Lightyear';

UPDATE movies SET 
    release_date = '2026-04-15 00:00:00',
    end_date = '2026-07-01 00:00:00'
WHERE title = 'Thor: Love and Thunder';

-- Update theater information
UPDATE theaters SET 
    city = 'New York',
    state = 'NY',
    pincode = '10001'
WHERE name = 'CineMax Downtown';

UPDATE theaters SET 
    city = 'Los Angeles',
    state = 'CA',
    pincode = '90001'
WHERE name = 'Movie Palace Central';

UPDATE theaters SET 
    city = 'Chicago',
    state = 'IL',
    pincode = '60601'
WHERE name = 'Cinema World Mall';

UPDATE theaters SET 
    city = 'Houston',
    state = 'TX',
    pincode = '77001'
WHERE name = 'Premium Theater';

-- Verify the updates
SELECT 'Movies with updated dates:' as Status;
SELECT title, release_date, end_date, 
       CASE 
           WHEN release_date <= NOW() AND end_date >= NOW() THEN 'Currently Showing'
           WHEN release_date > NOW() THEN 'Coming Soon'
           ELSE 'Ended'
       END as Status
FROM movies 
ORDER BY release_date;

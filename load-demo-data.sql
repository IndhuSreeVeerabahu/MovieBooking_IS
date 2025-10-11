-- Manual script to load demo data into the database
-- Run this script in your MySQL database if the automatic loading doesn't work

USE movie_ticket_booking;

-- Clear existing data (optional - remove if you want to keep existing data)
-- DELETE FROM booking_seats;
-- DELETE FROM bookings;
-- DELETE FROM shows;
-- DELETE FROM seats;
-- DELETE FROM screens;
-- DELETE FROM theaters;
-- DELETE FROM movies;
-- DELETE FROM users WHERE email != 'admin@moviehub.com';

-- Load the demo data
SOURCE src/main/resources/data.sql;

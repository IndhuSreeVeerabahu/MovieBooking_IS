# Complete Theater-Screen-Show Workflow Implementation Guide

## Overview
This guide explains how the theater, screen, and show components work together in your Movie Ticket Booking application and how to properly implement the complete user workflow.

## Architecture Overview

### Entity Relationships
```
Theater (1) -----> (Many) Screen (1) -----> (Many) Show
    |                    |                        |
    |                    |                        |
    v                    v                        v
(Many) Show         (Many) Seat              (Many) Booking
```

### Database Structure
- **theaters**: Contains theater information (name, location, amenities)
- **screens**: Contains screen information within theaters (screen number, type, capacity)
- **seats**: Contains individual seat information for each screen
- **shows**: Contains show information (movie, theater, screen, time, pricing)
- **bookings**: Contains booking information linking users to shows and seats

## Complete User Workflow

### 1. User Journey
```
Movies Page → Movie Details → Theater Selection → Showtime Selection → Seat Selection → Booking → Payment → Confirmation
```

### 2. Implementation Details

#### Step 1: Movies Page (`/movies`)
- **File**: `src/main/resources/templates/movies.html`
- **API**: `GET /api/movies`
- **Features**:
  - Lists all currently showing movies
  - Search and filter functionality
  - "Book Tickets" button navigates to movie details

#### Step 2: Movie Details Page (`/movie/{movieId}`)
- **File**: `src/main/resources/templates/movie-details.html`
- **APIs Used**:
  - `GET /api/theaters` - Get all theaters
  - `GET /api/theaters/city/{city}` - Get theaters by city
  - `GET /api/shows/movie/{movieId}/theater/{theaterId}` - Get shows for specific movie and theater
- **Features**:
  - Shows complete movie information
  - City selection dropdown
  - Date selection
  - Theater and showtime selection
  - "Book Tickets" button navigates to booking page

#### Step 3: Theater Selection
- **File**: `src/main/resources/templates/theaters.html`
- **API**: `GET /api/theaters`
- **Features**:
  - Lists all theaters with details
  - Search and filter by city/state
  - Theater amenities and contact information

#### Step 4: Booking Page (`/booking?showId={showId}`)
- **File**: `src/main/resources/templates/booking.html`
- **APIs Used**:
  - `GET /api/shows/{showId}` - Get show details
  - `GET /api/seat-layout/show/{showId}` - Get seat layout for show
  - `POST /api/booking` - Create booking
  - `POST /api/booking/{bookingId}/confirm` - Confirm booking with payment
- **Features**:
  - Shows movie and show information
  - Interactive seat selection
  - Real-time pricing calculation
  - Payment integration
  - Seat locking mechanism

## API Endpoints Reference

### Theater Endpoints
- `GET /api/theaters` - Get all active theaters
- `GET /api/theaters/city/{city}` - Get theaters by city
- `GET /api/theaters/{theaterId}` - Get specific theater
- `POST /api/theaters` - Create theater (Admin only)
- `PUT /api/theaters/{theaterId}` - Update theater (Admin only)
- `DELETE /api/theaters/{theaterId}` - Delete theater (Admin only)

### Screen Endpoints
- `GET /api/seat-layout/screen/{screenId}` - Get seat layout for screen
- `POST /api/seat-layout` - Create/update seat layout (Admin only)

### Show Endpoints
- `GET /api/shows/movie/{movieId}` - Get shows for movie
- `GET /api/shows/theater/{theaterId}` - Get shows for theater
- `GET /api/shows/movie/{movieId}/theater/{theaterId}` - Get shows for movie and theater
- `GET /api/shows/{showId}` - Get specific show
- `POST /api/shows` - Create show (Admin only)
- `PUT /api/shows/{showId}` - Update show (Admin only)
- `DELETE /api/shows/{showId}` - Delete show (Admin only)

### Booking Endpoints
- `GET /api/booking/shows` - Get available shows with filters
- `GET /api/booking/shows/{showId}/seats` - Get seat layout for show
- `POST /api/booking` - Create booking
- `POST /api/booking/{bookingId}/confirm` - Confirm booking with payment

## Key Features Implemented

### 1. Theater Management
- ✅ Multi-location support (city, state, pincode)
- ✅ Theater amenities tracking
- ✅ Active/inactive status management
- ✅ Search and filtering capabilities

### 2. Screen Management
- ✅ Multiple screen types (Standard, Premium, IMAX, Dolby Atmos, VIP)
- ✅ Flexible seat layout configuration
- ✅ Automatic seat generation
- ✅ Screen capacity management

### 3. Show Management
- ✅ Movie-theater-screen relationship
- ✅ Show scheduling with date/time
- ✅ Dynamic pricing based on seat type
- ✅ Show status management

### 4. Booking System
- ✅ Real-time seat availability
- ✅ Seat locking mechanism
- ✅ Dynamic pricing calculation
- ✅ Payment integration
- ✅ Booking confirmation

## How to Test the Complete Workflow

### 1. Start the Application
```bash
./mvnw spring-boot:run
```

### 2. Access the Application
- Navigate to `http://localhost:8080`
- Login with your credentials

### 3. Test the Workflow
1. **Movies Page**: Go to `/movies` to see all available movies
2. **Movie Details**: Click "Book Tickets" on any movie
3. **Theater Selection**: Select a city and date
4. **Showtime Selection**: Choose a theater and showtime
5. **Seat Selection**: Go to booking page and select seats
6. **Payment**: Complete the booking process

### 4. Admin Functions
- Go to `/admin/dashboard` for admin functions
- Create theaters, screens, and shows
- Manage seat layouts

## Troubleshooting Common Issues

### 1. "No theaters showing" in movie details
- **Cause**: No shows created for the selected movie/theater/date combination
- **Solution**: Create shows using admin panel or API

### 2. "No seats available" in booking
- **Cause**: Seat layout not configured for the screen
- **Solution**: Use `/api/seat-layout` endpoint to create seat layout

### 3. API errors in browser console
- **Cause**: Missing data or incorrect API calls
- **Solution**: Check browser developer tools and ensure all required data exists

### 4. Theater not appearing in city filter
- **Cause**: Theater not marked as active or missing city information
- **Solution**: Check theater status and city field in database

## Data Setup Requirements

### 1. Required Data
- At least one active theater
- At least one screen per theater
- At least one movie
- At least one show linking movie, theater, and screen
- Seat layout configured for screens

### 2. Sample Data Creation
Use the admin panel or API endpoints to create:
```json
// Theater
{
  "name": "PVR Cinemas",
  "address": "123 Mall Road",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400001",
  "phoneNumber": "+91-22-12345678",
  "email": "mumbai@pvr.com",
  "totalScreens": 4,
  "amenities": "IMAX, Dolby Atmos, Food Court"
}

// Screen
{
  "theaterId": 1,
  "screenNumber": 1,
  "screenName": "Screen 1",
  "totalRows": 10,
  "totalSeatsPerRow": 15,
  "screenType": "STANDARD"
}

// Show
{
  "movieId": 1,
  "theaterId": 1,
  "screenId": 1,
  "showDate": "2024-01-20T00:00:00",
  "showTime": "19:00:00",
  "endTime": "21:30:00",
  "basePrice": 250.00,
  "premiumPrice": 350.00,
  "vipPrice": 500.00
}
```

## Next Steps for Enhancement

1. **Real-time Updates**: Implement WebSocket for real-time seat availability
2. **Advanced Filtering**: Add more filter options (screen type, price range)
3. **Recommendations**: Add movie recommendations based on user history
4. **Mobile App**: Create mobile application using the same APIs
5. **Analytics**: Add booking analytics and reporting
6. **Notifications**: Email/SMS notifications for booking confirmations

## Support

If you encounter any issues:
1. Check the browser console for JavaScript errors
2. Check the application logs for backend errors
3. Verify that all required data exists in the database
4. Ensure all API endpoints are accessible
5. Check network connectivity and CORS settings

The complete workflow is now implemented and ready for use!

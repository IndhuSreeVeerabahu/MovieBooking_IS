# Export Functionality Guide

## Overview
The MovieHub admin panel now includes comprehensive export functionality for all major entities. Users can export data in both CSV and Excel formats directly from the admin interface.

## Features Implemented

### 1. Backend Export Service
- **Location**: `src/main/java/com/example/MovieTicketBooking/service/ExportService.java`
- **Dependencies Added**:
  - Apache POI (v5.2.4) for Excel export
  - OpenCSV (v5.8) for CSV export

### 2. Export Controller
- **Location**: `src/main/java/com/example/MovieTicketBooking/controller/ExportController.java`
- **Security**: Admin-only access with `@PreAuthorize("hasRole('ADMIN')")`
- **Endpoints**: All export endpoints follow the pattern `/api/admin/export/{entity}/{format}`

### 3. Supported Entities and Formats

#### Users Export
- **CSV**: `/api/admin/export/users/csv`
- **Excel**: `/api/admin/export/users/excel`
- **Fields**: ID, First Name, Last Name, Email, Phone Number, Role, Status, Created At, Updated At

#### Movies Export
- **CSV**: `/api/admin/export/movies/csv`
- **Excel**: `/api/admin/export/movies/excel`
- **Fields**: ID, Title, Description, Genre, Duration, Rating, Director, Cast, Language, Release Date, End Date, Active, Featured, Created At

#### Bookings Export
- **CSV**: `/api/admin/export/bookings/csv`
- **Excel**: `/api/admin/export/bookings/excel`
- **Fields**: ID, Booking Reference, User Email, Movie Title, Theater Name, Show Date, Total Amount, Booking Status, Payment Status, Payment Method, Booking Date, Created At

#### Theaters Export
- **CSV**: `/api/admin/export/theaters/csv`
- **Excel**: `/api/admin/export/theaters/excel`
- **Fields**: ID, Name, Address, City, State, Pincode, Phone Number, Email, Total Screens, Active, Created At, Updated At

#### Shows Export
- **CSV**: `/api/admin/export/shows/csv`
- **Excel**: `/api/admin/export/shows/excel`
- **Fields**: ID, Movie Title, Theater Name, Screen Name, Show Date, Show Time, Base Price, Premium Price, VIP Price, Show Status, Active, Created At

## Frontend Implementation

### 1. Admin Dashboard
- **Location**: `src/main/resources/templates/admin-dashboard.html`
- **Feature**: Comprehensive export menu with all entities and formats
- **UI**: Dropdown menu with organized export options

### 2. Individual Admin Panels
Each admin panel now includes export functionality:

#### User Management (`admin-users.html`)
- Export button with CSV/Excel dropdown
- Replaces the previous "Coming Soon" placeholder

#### Movie Management (`admin-movies.html`)
- Export button with CSV/Excel dropdown
- Integrated with existing movie management interface

#### Theater Management (`admin-theaters.html`)
- Export button with CSV/Excel dropdown
- Integrated with existing theater management interface

#### Show Management (`admin-shows.html`)
- Export button with CSV/Excel dropdown
- Integrated with existing show management interface

## User Experience Features

### 1. Loading States
- Buttons show "Exporting..." during download
- Buttons are disabled during export process
- Automatic reset after completion

### 2. File Naming
- Files are automatically named with timestamp: `{entity}_{YYYY-MM-DD_HH-mm-ss}.{format}`
- Example: `users_2024-01-15_14-30-25.xlsx`

### 3. Error Handling
- Network error handling with user-friendly messages
- Fallback notifications if notification system is unavailable
- Console logging for debugging

### 4. UI/UX Enhancements
- Dropdown menus with hover effects
- Click-outside-to-close functionality
- Consistent styling across all admin panels
- Success/error notifications

## Security Considerations

1. **Admin-Only Access**: All export endpoints require ADMIN role
2. **JWT Authentication**: Requests must include valid JWT token
3. **No Sensitive Data**: Passwords and other sensitive fields are excluded from exports

## Technical Implementation Details

### 1. Excel Export (Apache POI)
- Uses XSSFWorkbook for .xlsx format
- Auto-sizing columns for better readability
- Proper data type handling (dates, numbers, strings)

### 2. CSV Export (OpenCSV)
- UTF-8 encoding support
- Proper escaping of special characters
- Consistent field ordering

### 3. Date Formatting
- Consistent date format: `yyyy-MM-dd HH:mm:ss`
- Proper timezone handling
- Null-safe date formatting

### 4. File Download
- Blob-based download mechanism
- Automatic file cleanup after download
- Browser-compatible download handling

## Usage Instructions

### For Administrators:

1. **From Dashboard**:
   - Click "Export All Data" button
   - Select desired entity and format from dropdown
   - File will automatically download

2. **From Individual Panels**:
   - Navigate to any admin panel (Users, Movies, Theaters, Shows)
   - Click "Export [Entity]" button
   - Select CSV or Excel format
   - File will automatically download

### File Locations:
- Downloaded files will appear in your browser's default download folder
- Files are named with timestamps for easy organization

## Future Enhancements

Potential improvements for future versions:
1. **Filtered Exports**: Export only filtered/searched data
2. **Scheduled Exports**: Automated export scheduling
3. **Export History**: Track export activities
4. **Custom Field Selection**: Choose which fields to export
5. **Bulk Export**: Export multiple entities in one operation
6. **Email Integration**: Send exports via email

## Troubleshooting

### Common Issues:

1. **Export Fails**:
   - Check admin authentication
   - Verify network connection
   - Check browser console for errors

2. **File Not Downloading**:
   - Check browser download settings
   - Ensure popup blockers are disabled
   - Try different browser

3. **Empty Export**:
   - Verify data exists in database
   - Check entity relationships are properly loaded

### Support:
For technical issues, check the application logs and browser console for detailed error messages.

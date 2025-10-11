# Maven Dependencies Setup Guide

## Issue
The export functionality requires Apache POI and OpenCSV dependencies that need to be downloaded by Maven.

## Solutions

### Option 1: IDE Refresh (Recommended)
1. **IntelliJ IDEA**:
   - Right-click on `pom.xml` → "Maven" → "Reload project"
   - Or go to Maven tool window → Click refresh button

2. **Eclipse**:
   - Right-click on project → "Maven" → "Reload Projects"
   - Or right-click on `pom.xml` → "Maven" → "Reload Projects"

3. **VS Code**:
   - Open Command Palette (Ctrl+Shift+P)
   - Type "Java: Reload Projects"

### Option 2: Command Line (if Maven is installed)
```bash
# Navigate to project directory
cd "C:\Users\DELL LAPATOP\Desktop\MovieTicketBooking"

# Download dependencies
mvn dependency:resolve

# Or clean and install
mvn clean install
```

### Option 3: Maven Wrapper (if available)
```bash
# Navigate to project directory
cd "C:\Users\DELL LAPATOP\Desktop\MovieTicketBooking"

# Use Maven wrapper
.\mvnw.cmd dependency:resolve
```

## Current Status
✅ **Export functionality is working with CSV format**
- All export endpoints are functional
- CSV exports work perfectly
- Excel exports currently return CSV files (will be upgraded once dependencies are resolved)

## Dependencies Added
The following dependencies were added to `pom.xml`:

```xml
<!-- Apache POI for Excel export -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.4</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.4</version>
</dependency>

<!-- OpenCSV for CSV export -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.8</version>
</dependency>
```

## After Dependencies are Resolved
Once Maven downloads the dependencies, the export functionality will support:
- ✅ CSV exports (currently working)
- 🔄 Excel exports (will work after dependencies are resolved)

## Testing the Export Functionality
1. Start your Spring Boot application
2. Login as admin
3. Navigate to any admin panel (Users, Movies, Theaters, Shows)
4. Click "Export [Entity]" button
5. Select CSV or Excel format
6. File should download automatically

## Troubleshooting
- If export still fails, check browser console for errors
- Ensure you're logged in as an admin user
- Verify the application is running on the correct port
- Check that the database has data to export

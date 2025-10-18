@echo off
echo ========================================
echo   Movie Ticket Booking Application
echo ========================================
echo.

cd /d "C:\Users\DELL LAPATOP\Desktop\MovieTicketBooking"

echo Starting application...
echo Please wait for the application to start...
echo.

call mvnw.cmd spring-boot:run

echo.
echo Application stopped.
pause

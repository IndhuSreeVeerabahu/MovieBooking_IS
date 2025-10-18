@echo off
echo Starting Movie Ticket Booking Application (Simple Method)...
echo.

cd /d "C:\Users\DELL LAPATOP\Desktop\MovieTicketBooking"

echo Compiling with Maven wrapper...
.\mvnw.cmd clean compile -q

if %errorlevel% neq 0 (
    echo Compilation failed. Trying to run anyway...
)

echo Starting application...
.\mvnw.cmd spring-boot:run

pause

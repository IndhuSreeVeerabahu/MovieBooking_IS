@echo off
echo Starting Movie Ticket Booking Application...
echo.

cd /d "C:\Users\DELL LAPATOP\Desktop\MovieTicketBooking"

echo Checking for Maven...
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Maven not found in PATH. Trying Maven wrapper...
    if exist mvnw.cmd (
        echo Using Maven wrapper...
        call mvnw.cmd clean compile -q
        call mvnw.cmd spring-boot:run
    ) else (
        echo Maven not found. Please install Maven or use an IDE to run the application.
        echo You can also try running: java -jar target/*.jar
        pause
        exit /b 1
    )
) else (
    echo Using system Maven...
    call mvn clean compile -q
    call mvn spring-boot:run
)

pause

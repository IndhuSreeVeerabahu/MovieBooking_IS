# Movie Ticket Booking System - User Module

This is the first module of a comprehensive movie ticket booking system built with Spring Boot, featuring user authentication with JWT tokens.

## Features Implemented

### User Authentication
- ✅ User registration with validation (default role: USER)
- ✅ User login with JWT token generation
- ✅ JWT token validation and security
- ✅ Password encryption using BCrypt
- ✅ Role-based access control (USER/ADMIN)
- ✅ Beautiful dark-themed UI with Tailwind CSS

### User Management
- ✅ User profile management
- ✅ Admin panel for user management
- ✅ User status toggle (enable/disable)
- ✅ User deletion (Admin only)
- ✅ Thymeleaf-based web interface

### Security Features
- ✅ JWT-based authentication
- ✅ Form-based authentication for web pages
- ✅ CORS configuration
- ✅ Password validation
- ✅ Email validation
- ✅ Secure password encoding

## Technology Stack

- **Backend**: Spring Boot 3.5.6
- **Database**: MySQL 8.0
- **Security**: Spring Security with JWT
- **Validation**: Bean Validation
- **Code Generation**: Lombok
- **Frontend**: Thymeleaf with Tailwind CSS
- **UI Theme**: Dark theme inspired by BookMyShow

## Prerequisites

1. **Java 17** or higher
2. **MySQL 8.0** or higher
3. **Maven 3.6** or higher

## Database Setup

1. Install MySQL and create a database:
```sql
CREATE DATABASE movie_ticket_booking;
```

2. Update the database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Running the Application

1. **Clone and navigate to the project directory**

2. **Install dependencies and run the application**:
```bash
mvn clean install
mvn spring-boot:run
```

3. **Access the application**:
   - Web Interface: `http://localhost:8080`
   - Login Page: `http://localhost:8080/login`
   - Register Page: `http://localhost:8080/register`
   - Dashboard: `http://localhost:8080/dashboard` (after login)
   - Backend API: `http://localhost:8080/api`

## API Endpoints

### Authentication Endpoints (Public)
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/test` - Test JWT token (requires authentication)

### User Endpoints (Authenticated)
- `GET /api/user/profile` - Get current user profile
- `PUT /api/user/profile` - Update current user profile

### Admin Endpoints (Admin only)
- `GET /api/admin/users` - Get all users
- `PUT /api/admin/users/{userId}/toggle-status` - Toggle user status
- `DELETE /api/admin/users/{userId}` - Delete user

## Testing the Application

### Using the Web Interface
1. Open `http://localhost:8080` (redirects to login page)
2. Register a new user or login with existing credentials
3. Access the dashboard after successful login
4. View and update your profile
5. Test admin functionality (login with admin@moviehub.com / admin123)

### Default Admin Account
- **Email**: admin@moviehub.com
- **Password**: admin123
- **Role**: ADMIN

### Using Postman/curl

#### Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "phoneNumber": "+1234567890"
  }'
```

#### Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

#### Access protected endpoint:
```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Project Structure

```
src/main/java/com/example/MovieTicketBooking/
├── config/
│   └── SecurityConfig.java          # Security configuration
├── controller/
│   ├── AuthController.java          # Authentication endpoints
│   └── UserController.java          # User management endpoints
├── dto/
│   ├── AuthRequest.java             # Login request DTO
│   ├── AuthResponse.java            # Authentication response DTO
│   ├── RegisterRequest.java         # Registration request DTO
│   └── UserResponse.java            # User response DTO
├── entity/
│   └── User.java                    # User entity with JPA annotations
├── repository/
│   └── UserRepository.java          # User data access layer
├── security/
│   ├── JwtAuthenticationFilter.java # JWT authentication filter
│   └── JwtUtil.java                 # JWT utility class
├── service/
│   └── UserService.java             # User business logic
└── MovieTicketBookingApplication.java # Main application class
```

## Next Steps

This completes the User Module. The next modules to implement would be:

1. **Movie Module** - Movie management, genres, ratings
2. **Theater Module** - Theater management, locations
3. **Show Module** - Show scheduling, showtimes
4. **Seat Module** - Seat management, availability
5. **Booking Module** - Ticket booking, payment processing
6. **Notification Module** - Email confirmations, notifications

## Configuration Notes

- JWT secret key is configured in `application.properties`
- CORS is configured to allow requests from `localhost:3000` and `localhost:8080`
- Database tables are auto-created using Hibernate DDL
- Password minimum length is 6 characters
- JWT token expires in 24 hours (86400000 ms)

## Troubleshooting

1. **Database Connection Issues**: Ensure MySQL is running and credentials are correct
2. **Port Conflicts**: Change `server.port` in `application.properties` if 8080 is occupied
3. **CORS Issues**: Update CORS configuration in `SecurityConfig.java` for your frontend URL
4. **JWT Issues**: Check JWT secret key configuration and token expiration settings

# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for better caching
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Debug: Show Maven dependencies
RUN ./mvnw dependency:tree

# Copy source code
COPY src src

# Set environment variables for build process to prevent placeholder resolution errors
ENV DATABASE_URL=jdbc:h2:mem:testdb
ENV DB_USERNAME=sa
ENV DB_PASSWORD=
ENV JWT_SECRET=build-time-secret-key-for-testing-only
ENV CORS_ORIGINS=http://localhost:3000,http://localhost:8080
ENV MAIL_HOST=smtp.gmail.com
ENV MAIL_PORT=587
ENV MAIL_USERNAME=test@example.com
ENV MAIL_PASSWORD=test-password
ENV MAIL_FROM=noreply@example.com
ENV BASE_URL=http://localhost:8080
ENV CASHFREE_APP_ID=TEST108283821957fe1153788f32479528382801
ENV CASHFREE_SECRET_KEY=cfsk_ma_test_dddc2fa7d13c09a0a5f0c9c88f26f678_f1e97d01
ENV CASHFREE_ENVIRONMENT=SANDBOX
ENV SPRING_PROFILES_ACTIVE=prod

# Build the application with skip tests and H2 database for build process
RUN ./mvnw clean package -DskipTests -Dspring.profiles.active=prod -Dspring.datasource.url=jdbc:h2:mem:testdb -Dspring.datasource.driver-class-name=org.h2.Driver -Dspring.jpa.hibernate.ddl-auto=create-drop

# Debug: Show what was built
RUN ls -la target/

# Expose port
EXPOSE 8080

# Set environment variables for runtime
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication -Djdk.internal.platform.cgroupfs.disabled=true"
ENV SPRING_PROFILES_ACTIVE=prod

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

# Run the application with optimized startup
CMD ["sh", "-c", "java $JAVA_OPTS -jar target/MovieTicketBooking-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --server.port=8080"]

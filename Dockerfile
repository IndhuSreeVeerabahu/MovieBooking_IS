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

# Build the application
RUN ./mvnw clean package -DskipTests -Dspring.profiles.active=prod

# Debug: Show what was built
RUN ls -la target/

# Expose port
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar target/MovieTicketBooking-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod"]

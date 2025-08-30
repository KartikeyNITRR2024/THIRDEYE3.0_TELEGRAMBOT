# ===============================
# Stage 1: Build the application
# ===============================
FROM maven:3.8.5-openjdk-17 AS build

# Set working directory in the container
WORKDIR /app

# Copy pom.xml and download dependencies first (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy all source code
COPY src ./src

# Build the Spring Boot JAR (skip tests for faster build)
RUN mvn clean package -DskipTests

# ===============================
# Stage 2: Create a lightweight runtime image
# ===============================
FROM openjdk:17-slim

# Set working directory
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/THIRDEYE3.0_TELEGRAMBOT-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app uses
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

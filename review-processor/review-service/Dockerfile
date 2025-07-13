# Use a minimal Java 17 JDK base image
FROM eclipse-temurin:17-jdk-alpine as builder

# Set working directory
WORKDIR /app

# Copy build artifact
COPY build/libs/review-service-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8080

# Set default command
ENTRYPOINT ["java", "-jar", "app.jar"]

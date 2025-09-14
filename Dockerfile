# Stage 1: Build the application
# Use a specific, stable JDK version to build the project.
FROM eclipse-temurin:23-jdk as builder

# Set the working directory inside the container.
WORKDIR /app

# Copy only the necessary build files first to leverage Docker's build cache.
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Copy the source code last, as it changes most frequently.
COPY src ./src

# Make the Gradle wrapper executable and run the clean build.
RUN chmod +x ./gradlew && ./gradlew clean build -x test

# Stage 2: Create the final, lightweight runtime image
# Use a JRE-only base image for a smaller and more secure final image.
FROM eclipse-temurin:23-jre-alpine

# Create a non-root user for enhanced security.
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Set the working directory for the runtime.
WORKDIR /app

# Copy the built JAR file from the 'builder' stage.
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port.
EXPOSE 5004

# Define the command to run the application.
ENTRYPOINT ["java", "-jar", "app.jar"]
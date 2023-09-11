# Stage 1: Build the application
FROM gradle:8.2.1-jdk17 AS build

# Set the working directory
WORKDIR /home/gradle/src

# Copy the gradle configuration files
COPY --chown=gradle:gradle *.gradle ./
COPY --chown=gradle:gradle gradle ./gradle
COPY --chown=gradle:gradle build.gradle settings.gradle ./

# Copy the source code
COPY --chown=gradle:gradle src ./src

# Build the application
RUN gradle build --no-daemon

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar ./app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Use a base image with Java
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Maven or Gradle build output to the container
COPY target/task-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
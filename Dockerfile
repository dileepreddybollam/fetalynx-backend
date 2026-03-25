# Use official OpenJDK 24 image
FROM openjdk:24-jdk-slim

# Set working directory
WORKDIR /app

# Copy your jar file into container
COPY target/myapp.jar app.jar

# Expose port (change if needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
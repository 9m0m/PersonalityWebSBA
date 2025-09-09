# Use OpenJDK base image
FROM openjdk:21-jdk-slim

# Set app directory
WORKDIR /app

# Copy the JAR file
COPY target/personality-chat-ai-service-*.jar app.jar

# Expose app port
EXPOSE 8810

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

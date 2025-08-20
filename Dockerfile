FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradle gradle
COPY build.gradle .
COPY gradlew .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src src 

# Build the application (skip tests for faster build)
RUN ./gradlew build -x test

# Expose port (Railway will override this with the PORT environment variable)
EXPOSE $PORT

# Run the application with optimized JVM settings for Railway
CMD ["sh", "-c", "java -Xmx512m -Xms256m -Dserver.port=${PORT:-8080} -jar build/libs/backend-0.0.1-SNAPSHOT.jar"]
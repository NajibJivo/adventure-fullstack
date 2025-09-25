# Multi-stage build for optimal image size
FROM openjdk:17-jdk-slim as build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better layer caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies (cached layer if pom.xml hasn't changed)
RUN ./mvnw dependency:resolve

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Production stage
FROM eclipse-temurin:17-jre

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Create non-root user for security
RUN addgroup --system appuser && adduser --system --group appuser
USER appuser

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check (nu med curl installeret)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/api/activities || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
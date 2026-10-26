# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies first (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create non-root user for security
RUN useradd -r -u 1001 -g root cirms

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create uploads directory (used as fallback when Cloudinary is not configured)
RUN mkdir -p /app/uploads && chown -R cirms:root /app/uploads

USER cirms

EXPOSE 8080

# JVM tuning for containers (limits memory to what container allows)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]

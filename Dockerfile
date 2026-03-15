# STAGE 1: Build the application
FROM maven:3.9-eclipse-temurin-25-alpine AS builder
WORKDIR /build

# 1. Optimization: Copy only the POM first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy source and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# STAGE 2: Create the runtime image
FROM azul/zulu-openjdk-alpine:25-jre-headless
WORKDIR /app

# Best Practice: Run as a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Copy the JAR from the builder stage
# (Adjust 'target/*.jar' if your artifact name is specific)
COPY --from=builder /build/target/*.jar app.jar

# Optimized for container resources
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
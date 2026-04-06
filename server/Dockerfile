# BUILD
FROM gradle:9.4.0-jdk21-alpine AS builder
WORKDIR /app

# Copy first Gradle files for docker cache
# If dependencies don't change, this layer will be reuse
COPY build.gradle .
COPY settings.gradle .
COPY gradle/ gradle/
COPY gradlew .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon -q

COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test


# RUNTIME
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S trader && adduser -S trader -G trader
USER trader

# Copy final jar final from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

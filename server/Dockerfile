# BUILD
FROM gradle:9.4.0-jdk21-alpine AS builder
WORKDIR /app

# Copy first Gradle files for docker cache
# If dependencies don't change, this layer will be reuse
COPY build.gradle .
COPY settings.gradle .
COPY gradle/ gradle/
COPY gradlew .

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q

COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test


# RUNTIME
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# --no-cache refreshes the apk index before upgrading, so a rebuild is what actually
# pulls in patched Alpine packages (e.g. CVE-2026-14456 in openssl/libssl3/libcrypto3);
# the running image never patches itself, only a fresh build does.
RUN apk upgrade --no-cache && addgroup -S trader && adduser -S trader -G trader
USER trader

# Copy final jar final from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

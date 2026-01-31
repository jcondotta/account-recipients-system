FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY account-recipients-domain-service/pom.xml account-recipients-domain-service/pom.xml
COPY account-recipients-application-service/pom.xml account-recipients-application-service/pom.xml
COPY account-recipients-service/pom.xml account-recipients-service/pom.xml

RUN mvn dependency:go-offline -B

COPY account-recipients-domain-service account-recipients-domain-service
COPY account-recipients-application-service account-recipients-application-service
COPY account-recipients-service account-recipients-service

RUN mvn clean package \
    -pl account-recipients-service \
    -am \
    -DskipTests

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder \
  /app/account-recipients-service/target/account-recipients-service-*.jar \
  app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-XX:+UseG1GC","-XX:+ExitOnOutOfMemoryError","-jar","app.jar"]
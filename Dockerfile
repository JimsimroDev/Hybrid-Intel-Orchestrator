FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa final
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/hybrid-intel-orchestrator-0.0.1.jar app.jar
EXPOSE 5001
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn flyway:migrate jooq-codegen:generate clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:21-jdk-slim
EXPOSE 8080
WORKDIR /app
COPY --from=build /app/target/bip-backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
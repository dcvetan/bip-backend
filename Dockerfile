FROM openjdk:21-jdk-slim
EXPOSE 8080
COPY target/bip-backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
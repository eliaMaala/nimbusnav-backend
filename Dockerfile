FROM openjdk:17-jdk-slim
WORKDIR /app
COPY flight-management/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

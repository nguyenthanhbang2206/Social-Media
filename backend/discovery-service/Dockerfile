FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn -pl discovery-service -am clean package -DskipTests
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder \
    /app/discovery-service/target/*.jar \
    app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
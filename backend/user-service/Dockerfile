FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# copy toàn bộ project
COPY . .

RUN mvn -pl user-service -am clean package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/user-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
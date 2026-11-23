FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -B -pl account-service -am -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/account-service/target/account-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

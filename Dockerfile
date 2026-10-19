# Step 1: Build the JAR using Maven with Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Run the Spring Boot application on Java 17
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV PORT=9091
EXPOSE 9091
ENTRYPOINT ["java", "-jar", "app.jar"]
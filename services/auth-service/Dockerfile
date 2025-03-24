# --- Stage 1: Build the application using Maven ---
FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app
# Copy pom.xml and download dependencies (this layer is cached if pom.xml hasn’t changed)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the jar (skip tests if desired)
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Create a minimal runtime image ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the generated jar from the build stage; adjust the pattern if necessary.
COPY --from=build /app/target/*.jar app.jar

# Expose the port (default 8080 for auth-service)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
    
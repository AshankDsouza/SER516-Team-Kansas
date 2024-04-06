# Use a base image with JDK and then install Maven
FROM maven:3.8.3-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Download dependencies and build the project
RUN mvn dependency:go-offline

# Copy the source code
COPY src src

# Build the application
RUN mvn package -DskipTests

# Use a smaller base image without Maven and JDK for the runtime environment
FROM bellsoft/liberica-openjdk-alpine-musl:17

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage to the runtime image
COPY --from=build /app/target/*.jar app.jar

# Expose the port that the Spring Boot application uses
EXPOSE 8081

# Specify the command to run the application
CMD ["java", "-jar", "app.jar"]

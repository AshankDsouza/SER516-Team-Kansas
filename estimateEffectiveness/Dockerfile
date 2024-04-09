# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 as stage1
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
WORKDIR /opt/kansas/EstimateEffectivenessapp
# Copy the project files to the container
COPY . .
# Compile the source code and package it in a jar file
RUN mvn clean install
# List contents to verify the build output
RUN ls -la /opt/kansas/EstimateEffectivenessapp/target/

# Stage 2: Create the deployment image
FROM eclipse-temurin:17-jdk as stage2
WORKDIR /opt/kansas/EstimateEffectivenessapp
# Copy the built artifact from the Maven build
COPY --from=stage1 /opt/kansas/EstimateEffectivenessapp/target/EstimateEffectivenessapp-*.jar /opt/kansas/EstimateEffectivenessapp/EstimateEffectivenessapp.jar

RUN ls -la /opt/kansas/EstimateEffectivenessapp/

# Define the entry point for the application
ENTRYPOINT ["java","-jar","/opt/kansas/EstimateEffectivenessapp/EstimateEffectivenessapp.jar"]


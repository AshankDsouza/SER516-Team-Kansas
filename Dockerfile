#Stage 1
# initialize build and set base image for first stage
FROM maven:3.9.6-eclipse-temurin-17 as stage1
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
# set working directory
WORKDIR /opt/kansas/taiga
# copy just pom.xml
COPY . .
# go-offline using the pom.xml
#RUN mvn dependency:go-offline
# copy your other files
#COPY ./src ./src
# compile the source code and package it in a jar file
RUN mvn clean install -Dmaven.test.skip=true

#Stage 2
# set base image for second stage
FROM openjdk:17-jdk-alpine
# set deployment directory
WORKDIR /opt/kansas/taiga
# copy over the built artifact from the maven image
COPY --from=stage1 WORKDIR /opt/kansas/taiga/target/TaigaAPI-0.0.1-SNAPSHOT.jar /opt/kansas/taiga/
ENTRYPOINT ["java","-jar","TaigaAPI-0.0.1-SNAPSHOT.jar"]
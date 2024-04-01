#Stage 1
# initialize build and set base image for first stage
FROM maven:3.8.3-openjdk-17  as stage1
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
# set working directory
WORKDIR /opt/kansas/velocity
COPY . .
# compile the source code and package it in a jar file
RUN mvn clean install -Dmaven.test.skip=true -Ddockerfile.skip
#Stage 2
# set base image for second stage
FROM openjdk:17-jdk-alpine
# set deployment directory
WORKDIR /opt/kansas/velocity
# copy over the built artifact from the maven
COPY --from=stage1  /opt/kansas/velocity/target/velocity-0.0.1-SNAPSHOT.jar /opt/kansas/velocity/
ENTRYPOINT ["java","-jar","velocity-0.0.1-SNAPSHOT.jar"]
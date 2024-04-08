#Stage 1
# initialize build and set base image for first stage
FROM maven  as stage1
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
# set working directory
WORKDIR /opt/kansas/WorkCapacityapp
COPY . .
RUN echo $(ls -la ./)
# compile the ource code and package it in a jar file
RUN mvn clean install 
#Stage 2
# set base image for second stage
FROM eclipse-temurin:17-jdk
# set deployment directory
WORKDIR /opt/kansas/WorkCapacity
# copy over the built artifact from the maven
COPY --from=stage1  /opt/kansas/WorkCapacityapp/target/WorkCapacity-3.2.2.jar /opt/kansas/WorkCapacity/
ENTRYPOINT ["java","-jar","WorkCapacity-3.2.2.jar"]
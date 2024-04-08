#Stage 1
# initialize build and set base image for first stage
FROM maven  as stage1
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
# set working directory
WORKDIR /opt/kansas/taiga
COPY . .
# compile the source code and package it in a jar file
RUN mvn clean install -Dmaven.test.skip=true -Ddockerfile.skip
#Stage 2
# set base image for second stage
FROM openjdk
# set deployment directory
WORKDIR /opt/kansas/taiga
# copy over the built artifact from the maven
COPY --from=stage1  /opt/kansas/taiga/target/TaigaAPI-0.0.1-SNAPSHOT.jar /opt/kansas/taiga/
ENTRYPOINT ["java","-jar","TaigaAPI-0.0.1-SNAPSHOT.jar"]
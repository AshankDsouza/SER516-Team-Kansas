#Stage 1
# initialize build and set base image for first stage
FROM maven as stage1
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
# set working directory
WORKDIR /opt/kansas/cycleTime
COPY . .
# compile the source code and package it in a jar file
RUN mvn clean install -Dmaven.test.skip=true -Ddockerfile.skip
#CMD ["/bin/bash" ]
#Stage 2
# set base image for second stage
FROM openjdk
# set deployment directory
WORKDIR /opt/kansas/cycleTime
# copy over the built artifact from the maven
COPY --from=stage1  /opt/kansas/cycleTime/target/cycleTime-3.2.2.jar /opt/kansas/cycleTime/
ENTRYPOINT ["java","-jar","cycleTime-3.2.2.jar"]
#CMD ["/bin/bash" ]
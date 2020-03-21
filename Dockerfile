# Use maven to build jar
FROM maven:3-jdk-8 as build
# Maintainer
MAINTAINER Robert Franzke (r.l.franzke@gmail.com)
# Copy files
COPY src /home/app/src
COPY pom.xml /home/app
# Run maven
RUN mvn -f /home/app/pom.xml package


# Run spring in minimal container
FROM openjdk:8-jre-alpine
# Maintainer
MAINTAINER Robert Franzke (r.l.franzke@gmail.com)
# Copy jar file
COPY --from=build /home/app/target/backend-*.jar /root/backend.jar
# Execute
CMD ["java","-jar","/root/backend.jar"]

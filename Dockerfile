# Use maven to build jar
FROM maven:3-jdk-8 as build
# Copy files
COPY src /home/app/src
COPY pom.xml /home/app
# Run maven
RUN mvn -Dmaven.test.skip=true -f /home/app/pom.xml package


# Run spring in minimal container
FROM openjdk:8-jre-alpine
# Version and commit arguments
ARG VERSION=unkown
ENV app.version=$VERSION
ARG COMMIT=unkown
ENV app.commit=$VERSION
# Copy jar file
COPY --from=build /home/app/target/backend-*.jar /root/backend.jar
# Execute
CMD ["java","-jar","/root/backend.jar"]

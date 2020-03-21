FROM openjdk:8-jdk
MAINTAINER Robert Franzke (r.l.franzke@gmail.com)
RUN apt-get update
RUN apt-get install -y maven
COPY pom.xml /usr/local/service/pom.xml
COPY src /usr/local/service/src
WORKDIR /usr/local/service
RUN mvn package
CMD ["java","-jar","target/backend-0.0.1-SNAPSHOT.jar"]
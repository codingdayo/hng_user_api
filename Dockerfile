#FROM ubuntu:latest
#LABEL authors="dayoawoniyi"
#
#ENTRYPOINT ["top", "-b"]

FROM openjdk:17
ARG JAR_FILE=target/*jar
EXPOSE 8080
COPY ./target/user-docker.jar user-docker.jar
ENTRYPOINT ["java","-jar","/user-docker.jar"]


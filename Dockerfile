#FROM ubuntu:latest
#LABEL authors="dayoawoniyi"
#
#ENTRYPOINT ["top", "-b"]

#FROM openjdk:17
#ARG JAR_FILE=target/*jar
#EXPOSE 8080
#COPY ./target/user-docker.jar user-docker.jar
#ENTRYPOINT ["java","-jar","/user-docker.jar"]
#

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17

# Set the working directory in the container
WORKDIR /src

# Argument for the JAR file
ARG JAR_FILE=target/user-docker.jar

# Copy the JAR file to the container
COPY ${JAR_FILE} src.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "src.jar"]
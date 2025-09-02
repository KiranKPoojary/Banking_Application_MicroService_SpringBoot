# Use a base image with a Java runtime.
FROM openjdk:21-jdk-slim

# Set the working directory inside the container.
WORKDIR /app

# The ARG instruction defines a variable that can be passed at build time.
# This makes your Containerfile more flexible.
ARG JAR_FILE=target/*.jar

# The COPY instruction copies the JAR from your local filesystem into the container's
# /app directory and renames it to `app.jar`.
COPY ${JAR_FILE} app.jar

# EXPOSE declares the port on which the container will listen.
# This is mainly for documentation purposes.
EXPOSE 8081

# The ENTRYPOINT command specifies the command to execute when the container starts.
# It runs your application with the java -jar command.
ENTRYPOINT ["java","-jar","app.jar"]
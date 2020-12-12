FROM  adoptopenjdk/openjdk13:jdk-13.0.2_8-alpine-slim

RUN apk update
RUN apk upgrade
RUN apk add bash

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY wait-for-it.sh wait-for-it.sh
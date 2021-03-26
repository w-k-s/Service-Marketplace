FROM  adoptopenjdk/openjdk13:alpine-jre

RUN apk update
RUN apk upgrade
RUN apk add bash

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY wait-for-it.sh wait-for-it.sh
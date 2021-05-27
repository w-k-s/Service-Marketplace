FROM adoptopenjdk/openjdk15:alpine-jre

RUN apk update
RUN apk upgrade
RUN apk add bash
RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*

HEALTHCHECK --interval=30s --timeout=4s CMD curl -f http://localhost:8080/health || exit 1


ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY wait-for-it.sh wait-for-it.sh
CMD ["java", "-jar", "app.jar"]
FROM  adoptopenjdk/openjdk13:alpine-jre

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

CMD ["java", "-jar", "app.jar"]
FROM openjdk:17

ARG JAR_FILE=build/libs/onspring-customer-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
COPY .env .env

ENTRYPOINT ["java", "-jar", "app.jar"]
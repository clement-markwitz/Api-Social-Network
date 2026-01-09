FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY ./jar/api-rest-0.0.1-SNAPSHOT.jar /app/api-rest-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/api-rest-0.0.1-SNAPSHOT.jar"]
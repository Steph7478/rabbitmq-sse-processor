FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

RUN apk add --no-cache maven

COPY ../../pom.xml .
COPY ../../src ./src

RUN mvn quarkus:go-offline

EXPOSE 8080 5005

CMD ["sh", "-c", "mvn quarkus:dev -Dquarkus.http.host=0.0.0.0 -Ddebug=5005 -DdebugHost=0.0.0.0"]

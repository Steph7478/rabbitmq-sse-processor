FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

RUN apk add --no-cache maven

COPY ../../pom.xml .
COPY ../../src ./src

ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -Dquarkus.http.port=8080 -Dquarkus.http.host=0.0.0.0"

EXPOSE 8080 5005

CMD ["mvn", "quarkus:dev", "-Dquarkus.http.host=0.0.0.0", "-Ddebug=5005"]
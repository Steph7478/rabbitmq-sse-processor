FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY ../../target/quarkus-app /app/quarkus-app

EXPOSE 8080

CMD ["java", "-jar", "/app/quarkus-app/quarkus-run.jar"]
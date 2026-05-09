# Estágio 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

RUN apk add --no-cache maven

COPY ../../pom.xml .
COPY ../../src ./src

RUN mvn package -Pnative -Dquarkus.native.container-build=true \
    -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21

FROM registry.access.redhat.com/ubi8/ubi-minimal:latest

WORKDIR /app

COPY --from=builder /app/target/*-runner /app/application

EXPOSE 8080

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
# RabbitMQ SSE Processor

A reactive message processing system using RabbitMQ and Server-Sent Events (SSE) for real-time status notifications.

## Quick Start

### Using Makefile

make dev          # Start development mode (hot-reload + debug)
make prod-native  # Start production mode (Native Image)
make prod-jvm     # Start production mode (JVM)
make stop         # Stop all services
make clean        # Stop and remove volumes
make logs-dev     # Show dev logs
make status       # Show service status

### Using Docker Compose

cd docker/dev
docker-compose up --build

### API Testing

Use the `api-processing.http` file to test all endpoints with IntelliJ IDEA, VS Code (REST Client extension), or any HTTP client.

## Requirements

- Docker & Docker Compose
- Java 21
- Maven

## Access Points

- API: http://localhost:8080
- RabbitMQ Management: http://localhost:15672 (guest/guest)
- Dev UI: http://localhost:8080/q/dev-ui
- Debug port: 5005 (dev mode only)
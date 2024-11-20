# Microservices Delivery System

A microservices-based delivery system built with Spring Boot and Docker.

## Architecture

The system consists of two main services:

- **API Gateway (Port 8888)**: Routes and manages requests to the email service
- **Email Service (Port 8081)**: Handles email processing and sending
- **Identity Service (Port 8080)**: Handles identity and authentication

## Prerequisites

- Java 23
- Docker
- Gradle

## Building and Running

1. Build the services:

```bash
docker compose build
```

2. Run the services:

```bash
docker compose up
```

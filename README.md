# NGINX Load Balancer with Spring Boot and Docker

A hands-on system design project demonstrating reverse proxying, round-robin load balancing, horizontal scaling, and fault tolerance using NGINX, Spring Boot, and Docker Compose.

## Technology Stack

- Java 25
- Spring Boot 4
- NGINX
- Docker
- Docker Compose
- Maven
- REST API

## Architecture

```text
             Client
                |
         localhost:8080
                |
          NGINX Proxy
                |
         Load Balancer
           /       \
          /         \
   Spring Boot   Spring Boot
    Backend 1     Backend 2
    Port 8081     Port 8082
```

NGINX acts as a reverse proxy and distributes incoming HTTP requests between two Spring Boot application instances using round-robin load balancing.

## Features

- RESTful API using Spring Boot
- NGINX reverse proxy
- Round-robin load balancing
- Multiple backend instances
- Docker containerization
- Docker Compose orchestration
- Basic fault tolerance and backend recovery

## Running the Project

### Prerequisites

- Docker Desktop
- Docker Compose

### Start the application

```bash
docker compose up --build -d
```

### Verify containers

```bash
docker compose ps
```

### Test load balancing

```bash
for i in {1..10}; do
    curl -s http://localhost:8080/hello
    echo
done
```

Example output:

```text
Hello from Spring Boot on port 8081
Hello from Spring Boot on port 8082
Hello from Spring Boot on port 8081
Hello from Spring Boot on port 8082
```

## Fault Tolerance Testing

Stop the first backend:

```bash
docker compose stop backend1
```

Send requests again:

```bash
curl http://localhost:8080/hello
```

After NGINX detects the unavailable backend, requests should continue being served by backend2.

Restart the backend:

```bash
docker compose start backend1
```

NGINX can subsequently route requests to both instances again.

## Key Concepts Demonstrated

**Reverse Proxy:** NGINX provides a single entry point and forwards HTTP requests to backend services.

**Load Balancing:** Requests are distributed across backend instances using round robin.

**Horizontal Scaling:** Multiple instances of the same application handle incoming traffic.

**Fault Tolerance:** An available backend can continue serving traffic when another instance fails.

**Container Networking:** Docker Compose enables service-to-service communication using internal DNS.

## Stop the Application

```bash
docker compose down
```

## Future Improvements

- NGINX rate limiting
- Redis caching
- Prometheus and Grafana monitoring
- HTTPS and TLS termination
- Kubernetes deployment
- Automated CI/CD pipelines

## Purpose

This project was developed to gain practical experience with backend infrastructure and distributed system design concepts relevant to software engineering interviews.
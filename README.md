# Spring Boot AI Chat Application with NGINX, Docker & Kubernetes

A containerized AI-powered backend application built with **Java 25, Spring Boot 4, NGINX, Docker, Kubernetes, and Ollama**.

This project demonstrates backend development, REST API integration with a locally hosted large language model (LLM), reverse proxy load balancing, container orchestration, horizontal scaling, self-healing, and application health monitoring.

## Project Overview

The application provides REST endpoints for basic backend testing and AI-powered chat using the **Llama 3.2 (3B)** model through Ollama.

It supports two deployment approaches:

- **Docker Compose:** Runs two Spring Boot backend instances behind an NGINX reverse proxy.
- **Kubernetes:** Runs multiple Spring Boot replicas behind a Kubernetes Service, with scaling, self-healing, and health probes.

Ollama currently runs on the host machine and is shared by the backend instances.

## Technology Stack

| Category | Technology |
|---|---|
| Programming Language | Java 25 |
| Backend Framework | Spring Boot 4.0.8 |
| Build Tool | Maven |
| REST API | Spring Web MVC |
| AI Integration | Ollama REST API |
| Language Model | Llama 3.2 (3B) |
| Reverse Proxy | NGINX |
| Containerization | Docker |
| Local Orchestration | Docker Compose |
| Container Orchestration | Kubernetes |
| Health Monitoring | Spring Boot Actuator |
| Version Control | Git and GitHub |

## Features

### Spring Boot REST API

- Lightweight REST endpoints.
- AI-powered text generation.
- Integration with a locally running Ollama server.
- Backend instance information included in AI responses.

### NGINX Load Balancing

- Reverse proxy routing to multiple Spring Boot containers.
- Round-robin load balancing.
- Backend failover when an instance becomes unavailable.

### Docker Containerization

- Containerized Spring Boot application.
- Docker Compose configuration for multiple backend instances.
- Reproducible local development environment.

### Kubernetes Orchestration

- Kubernetes Deployment for managing Spring Boot pods.
- Kubernetes ClusterIP Service for internal load balancing.
- Horizontal scaling from 2 to 5 backend replicas.
- Automatic pod replacement through Deployment reconciliation.
- Liveness and readiness probes.
- Support for rolling updates and rollbacks through Kubernetes Deployments.

### AI Integration

- Local LLM inference using Ollama.
- Llama 3.2 (3B) model.
- REST-based prompt submission and generated responses.
- Shared Ollama instance accessible from Docker and Kubernetes environments.

## System Architecture

### Docker Compose Architecture

```text
                  Client
                    |
                    v
             NGINX Reverse Proxy
               localhost:8080
                    |
             Load Balancing
               /         \
              v           v
       Spring Boot    Spring Boot
         Backend 1      Backend 2
         Port 8081      Port 8082
              \           /
               \         /
                v       v
               Ollama Server
               Llama 3.2:3b
               Host Port 11434
```

### Kubernetes Architecture

```text
                  Client
                    |
                    v
           kubectl port-forward
                    |
                    v
             Kubernetes Service
                    |
             Load Balancing
                    |
          Kubernetes Deployment
              Replicas: 5
                    |
       +------------+------------+
       |            |            |
       v            v            v
    Pod 1         Pod 2       Pods 3-5
  Spring Boot   Spring Boot   Spring Boot
       |            |            |
       +------------+------------+
                    |
                    v
              Ollama Server
              Llama 3.2:3b
              Running on Mac
```

Kubernetes Services distribute connections across ready backend pods. The Ollama model runs outside the Kubernetes cluster in the current local setup.

## Prerequisites

Install the following tools:

- Java 25
- Maven or the included Maven Wrapper
- Docker Desktop
- Kubernetes cluster (Docker Desktop Kubernetes or another compatible local cluster)
- kubectl
- Ollama
- Git

Check your installations:

```bash
java -version
docker --version
docker compose version
kubectl version --client
ollama --version
```

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/patelkrushi83/nginx-load-balancer-springboot.git

cd nginx-load-balancer-springboot
```

### 2. Download the Ollama Model

```bash
ollama pull llama3.2:3b
```

Verify that the model is installed:

```bash
ollama list
```

Start Ollama if it is not already running:

```bash
ollama serve
```

Ollama's default API address is:

```text
http://localhost:11434
```

Verify connectivity:

```bash
curl http://localhost:11434/api/tags
```

If Ollama is already running, you do not need to start another server.

## Option 1: Run with Docker Compose

Build and start the application:

```bash
docker compose up --build -d
```

Check the running containers:

```bash
docker compose ps
```

The Docker Compose setup includes:

- NGINX reverse proxy on localhost port 8080.
- Spring Boot backend 1 on port 8081.
- Spring Boot backend 2 on port 8082.

Test the application:

```bash
curl http://localhost:8080/hello
```

Test AI chat:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Explain Docker in one sentence."}'
```

Example response:

```json
{
  "response": "Docker is a platform for packaging and running applications in containers.",
  "backend": "8081"
}
```

The generated text and backend instance may vary.

Stop the containers:

```bash
docker compose down
```

## Option 2: Run with Kubernetes

The Kubernetes deployment uses a local Spring Boot image and connects to Ollama through the host.

### 1. Build the Docker Image

```bash
docker build -t nginx-demo-backend:v2 .
```

The image must be available to your Kubernetes node. Depending on your local Kubernetes configuration, an additional image-loading step may be required.

### 2. Deploy the Application

```bash
kubectl apply -f k8s/deployment.yaml
```

Create the Kubernetes Service:

```bash
kubectl apply -f k8s/service.yaml
```

### 3. Verify the Deployment

```bash
kubectl get deployments
```

```bash
kubectl get pods
```

```bash
kubectl get services
```

Check the rollout:

```bash
kubectl rollout status deployment/nginx-demo-backend
```

The deployment configuration targets five Spring Boot replicas.

### 4. Access the Application

Forward a local port to the Kubernetes Service:

```bash
kubectl port-forward service/nginx-demo-service 9092:80
```

Keep this terminal running.

In another terminal, test:

```bash
curl http://localhost:9092/hello
```

Test AI chat:

```bash
curl --max-time 120 -X POST http://localhost:9092/api/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Explain Kubernetes in one sentence."}'
```

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/hello` | Basic Spring Boot response |
| POST | `/api/chat` | Generate an AI response using Ollama |
| GET | `/actuator/health` | Application health |
| GET | `/actuator/health/liveness` | Kubernetes liveness check |
| GET | `/actuator/health/readiness` | Kubernetes readiness check |

### AI Chat Request

```json
{
  "prompt": "What is load balancing?"
}
```

### AI Chat Response

```json
{
  "response": "Load balancing distributes incoming traffic across multiple servers.",
  "backend": "8080"
}
```

The `backend` field currently represents the application's configured port, not its unique Kubernetes pod identity.

## Kubernetes Horizontal Scaling

Kubernetes allows the number of backend replicas to be changed without manually creating containers.

Scale to five replicas:

```bash
kubectl scale deployment nginx-demo-backend --replicas=5
```

Check the result:

```bash
kubectl get deployments
```

Watch the pods:

```bash
kubectl get pods -w
```

Scale back to two replicas:

```bash
kubectl scale deployment nginx-demo-backend --replicas=2
```

Manual scaling changes the live Deployment. Updating `k8s/deployment.yaml` is necessary if the desired replica count should also be preserved in source control.

## Kubernetes Self-Healing

Kubernetes Deployments maintain the configured number of replicas.

To demonstrate automatic pod replacement, delete one backend pod:

```bash
kubectl delete pod $(kubectl get pods -l app=nginx-demo-backend -o jsonpath='{.items[0].metadata.name}')
```

Watch Kubernetes create a replacement:

```bash
kubectl get pods -w
```

Kubernetes automatically creates a new pod to restore the desired replica count.

## Liveness and Readiness Probes

Spring Boot Actuator provides health endpoints used by Kubernetes.

### Liveness Probe

```text
/actuator/health/liveness
```

The liveness probe determines whether the application should be restarted after repeated health check failures.

### Readiness Probe

```text
/actuator/health/readiness
```

The readiness probe determines whether a pod should receive traffic through Kubernetes Services.

Test readiness:

```bash
curl http://localhost:9092/actuator/health/readiness
```

Test liveness:

```bash
curl http://localhost:9092/actuator/health/liveness
```

Expected response:

```json
{
  "status": "UP"
}
```

## Kubernetes Rolling Updates and Rollbacks

Kubernetes Deployments support controlled application updates.

Check deployment history:

```bash
kubectl rollout history deployment/nginx-demo-backend
```

Monitor an update:

```bash
kubectl rollout status deployment/nginx-demo-backend
```

Roll back to the previous Deployment revision when available:

```bash
kubectl rollout undo deployment/nginx-demo-backend
```

These commands are supported by Kubernetes; a complete rolling-update and rollback demonstration is a planned project exercise.

## Useful Kubernetes Commands

| Command | Purpose |
|---|---|
| `kubectl get nodes` | List cluster nodes |
| `kubectl get pods` | List pods |
| `kubectl get deployments` | List Deployments |
| `kubectl get services` | List Services |
| `kubectl describe pod POD_NAME` | Inspect a pod |
| `kubectl logs deployment/nginx-demo-backend` | View application logs |
| `kubectl get pods -w` | Watch pod changes |
| `kubectl rollout status deployment/nginx-demo-backend` | Check rollout progress |

## Project Structure

```text
nginx-demo/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/nginx_demo/
│       │       ├── ChatController.java
│       │       ├── service/
│       │       │   └── OllamaService.java
│       │       └── ...
│       └── resources/
│           └── application.properties
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
├── Dockerfile
├── compose.yaml
├── nginx.conf
├── pom.xml
├── mvnw
└── README.md
```

## Current Limitations

- Ollama runs on the host machine rather than inside Kubernetes.
- The Kubernetes deployment is designed for local development.
- The application does not currently include user authentication or persistent chat history.
- Kubernetes replicas share one Ollama inference server, so increasing backend replicas does not automatically increase LLM inference capacity.
- Automatic scaling based on CPU utilization has not yet been configured.
- The project does not currently implement document retrieval or Retrieval-Augmented Generation (RAG).

## Planned Enhancements

- React and TypeScript frontend.
- Spring Security and JWT authentication.
- PostgreSQL for persistent chat history.
- PDF document upload and text extraction.
- Retrieval-Augmented Generation using embeddings and vector search.
- Streaming AI responses.
- Redis-based caching and rate limiting.
- Kubernetes Horizontal Pod Autoscaler.
- Prometheus and Grafana monitoring.
- Automated testing and GitHub Actions CI/CD.

## Learning Outcomes

This project provides hands-on experience with:

- Designing REST APIs with Spring Boot.
- Integrating an LLM through a REST API.
- Dockerizing Java applications.
- Configuring NGINX as a reverse proxy and load balancer.
- Deploying containerized applications on Kubernetes.
- Managing replicas and Kubernetes Services.
- Testing horizontal scaling and self-healing.
- Configuring liveness and readiness probes.
- Troubleshooting container networking and application connectivity.

## Author

**Krushi Patel**

GitHub: https://github.com/patelkrushi83

## License

No license has been specified for this repository.
## LLM Integration with Ollama

This project integrates a locally hosted Large Language Model (LLM) using Ollama and Llama 3.2 (3B).

### Updated Architecture

```text
                  Client
                     |
                     v
              NGINX (8080)
                     |
          +----------+----------+
          |                     |
          v                     v
   Spring Boot 1          Spring Boot 2
      (8081)                 (8082)
          |                     |
          +----------+----------+
                     |
                     v
              Ollama Server
             (Mac Host: 11434)
                     |
                     v
               Llama 3.2 3B
```

### Features

- RESTful AI chat API using Spring Boot.
- Integration with Llama 3.2 through Ollama.
- NGINX round-robin load balancing across two backend instances.
- Docker Compose orchestration.
- Backend identification in API responses.
- Backend fault-tolerance testing.
- Structured application logging.

### Prerequisites

- Java 25
- Maven
- Docker Desktop
- Ollama
- Llama 3.2 (3B)

### Running the Application

Start Ollama on your Mac and download the model if it is not already installed:

```bash
ollama pull llama3.2:3b
```

Ensure the Ollama server is running on port `11434`.

Start the Docker containers:

```bash
docker compose up --build -d
```

### Test the AI Chat API

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Explain NGINX load balancing."}'
```

Example response:

```json
{
  "response": "NGINX distributes incoming traffic across multiple backend servers.",
  "backend": "8081"
}
```

### Test Load Balancing

Send repeated requests to `/api/chat` and inspect the `backend` field to verify that requests reach both Spring Boot instances.

### Test Fault Tolerance

Stop one backend:

```bash
docker compose stop backend1
```

Verify that AI requests continue through the remaining backend.

Restore the stopped backend:

```bash
docker compose start backend1
```

### Architecture Limitations

Both Spring Boot instances share a single Ollama server running on the host machine. This provides backend redundancy but not LLM inference redundancy.

Future improvements include asynchronous inference, rate limiting, model observability, request authentication, and multiple inference servers.
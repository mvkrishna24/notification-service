# Notification Service

> A production-grade, horizontally scalable real-time notification delivery platform.

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?logo=redis)
![Kafka](https://img.shields.io/badge/Apache_Kafka-3.5-231F20?logo=apachekafka)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)

## What it does

Any producer application can POST an event to this service. The event is ingested via a REST API, deduplicated with Redis, queued in Kafka, and asynchronously delivered to users over WebSocket, SSE, Email, or Webhook — with retries, a dead-letter queue, and Prometheus metrics.

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 17, Spring Boot 3.3 |
| Database | PostgreSQL 16 + Flyway migrations |
| Cache / Dedup / Rate limiting | Redis 7 |
| Async delivery | Apache Kafka (single-broker dev) |
| Real-time channels | Spring WebSocket, Spring SSE |
| Email | Spring Mail → Mailtrap (dev) |
| Observability | Micrometer + Prometheus + structured JSON logs |
| Load testing | k6 |
| CI/CD | GitHub Actions |
| Deployment | Render |

## Running Locally

**Prerequisites:** Docker Desktop, Java 17+, Maven 3.9+

```bash
# 1. Clone and enter
git clone https://github.com/mvkrishna24/notification-service.git
cd notification-service

# 2. Start all infrastructure
make up

# 3. Create Kafka topics (once Kafka is healthy ~30s)
make kafka-topics

# 4. Verify everything is up
docker-compose ps
```

Service endpoints once running:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/prometheus

## Architecture

```
Producer → POST /api/v1/events → [Redis dedup] → Kafka
                                                      ↓
                                             Delivery Worker
                                            ↙   ↙    ↓    ↘
                                       WS  SSE Email Webhook
```

See [docs/design-decisions.md](docs/design-decisions.md) for architectural choices.

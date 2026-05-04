# Design Decisions

This document records non-obvious architectural choices made during the build.
Each entry explains the decision, the alternatives considered, and why this path was taken.

---

## Phase 1 — Project Setup

### 1. Why `kafka/` is a top-level package, not nested inside `service/`

The `service/` package holds business-domain logic: things like "process this event" or
"check if a user is online." Kafka is infrastructure — it's the transport layer, not the
business layer. A Kafka `@KafkaListener` is closer to a controller (it receives inbound
messages from the broker) than it is to a service (which encodes domain rules).

Keeping `kafka/producer/` and `kafka/consumer/` separate signals to any reader of this
codebase that you understand the distinction between message infrastructure and business
logic. It also makes it easy to swap the transport (e.g., replace Kafka with RabbitMQ)
without touching `service/`.

Alternatives considered:
- Put listeners inside `service/` — blurs infrastructure/domain boundary.
- Use a flat `messaging/` package — loses the producer/consumer distinction.

---

### 2. Confluent `cp-kafka:7.5.0` vs `apache/kafka` Docker image

**Confluent Platform images** (confluentinc/cp-kafka) ship with the full Confluent
tooling preinstalled — `kafka-topics`, `kafka-console-producer`, `kafka-console-consumer`,
and Schema Registry clients are all on the PATH inside the container. This makes local
debugging fast: `docker exec notification-kafka kafka-topics --list` just works.

**apache/kafka** images (the official Apache image introduced in Kafka 3.7) are leaner
but ship fewer tools. They also default to KRaft mode (no Zookeeper), which is the right
long-term direction but adds configuration surface area during early development.

For a dev environment where you want `make kafka-topics` to just work and diagnostics to
be frictionless, Confluent is the practical choice. The Confluent image version (7.5.0)
maps to Apache Kafka 3.5.x internally, which is production-quality and stable.

Alternatives considered:
- `apache/kafka:3.7` in KRaft mode — removes Zookeeper dependency but requires more
  bootstrap config and means fewer diagnostic tools available in-container.
- `bitnami/kafka` — convenient but adds a third-party layer with its own config schema.

---

### 3. JDK 25 on the machine, `--release 17` in Maven

The dev machine has JDK 25 installed (Temurin). The project sets `<java.version>17</java.version>`
in pom.xml, which causes Maven to compile with `javac --release 17`. This flag does three things:
- Restricts syntax to Java 17 features only (no Records text blocks from 21+).
- Sets the bytecode target to class file version 61 (Java 17).
- Enforces correct API visibility (can't accidentally use JDK 18+ APIs).

The Docker build uses `eclipse-temurin:17` so the CI/production environment exactly
matches the language level we're targeting. Having a newer JDK locally is harmless.

---

### 4. Dual Kafka listener setup (PLAINTEXT + PLAINTEXT_HOST)

Kafka's advertised listeners determine what address a client receives when it asks the
broker "how should I connect to you?" This creates a problem in Docker: if Kafka
advertises `kafka:9092`, a host-machine client can't resolve `kafka` as a hostname.

Solution: two listeners with different purposes.
- `PLAINTEXT://kafka:29092` — advertised to container-to-container traffic.
  The Spring Boot app service in Docker uses `KAFKA_BOOTSTRAP_SERVERS=kafka:29092`.
- `PLAINTEXT_HOST://localhost:9092` — advertised to external/host traffic.
  When developing with the app running locally (outside Docker), use `localhost:9092`.

Port 9092 is mapped from the host to the container's 9092 (PLAINTEXT_HOST listener).
Port 29092 is internal only — never exposed to the host.

---

### 5. `spring.jpa.open-in-view: false`

Spring Boot defaults this to `true`. With open-in-view enabled, the JPA EntityManager
(and therefore a database connection) is held open for the entire HTTP request lifecycle.
This was designed for template-rendered apps (Thymeleaf, JSF) where lazy-loading in the
view layer is common.

For a REST API like this one, it's harmful:
- Holds a DB connection far longer than needed, reducing connection pool capacity under load.
- Hides N+1 bugs at development time because lazy-loading "just works" in dev, then
  collapses at scale when the pool is exhausted.
- Causes Spring Boot to log a warning at startup if not explicitly set.

Setting it to `false` forces all data fetching to happen inside the transaction boundary
(inside the `@Service` method), which is the correct behavior.

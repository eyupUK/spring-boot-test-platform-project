# Test Platform Project
A **Test Enablement** Project with Java/Spring Boot.

## What's inside
- **orchestrator-service** (Spring Boot): accepts a test run request, plans a tiny DAG, calls the **data-broker** for personas, and publishes a Kafka event.
- **data-broker-service** (Spring Boot): exposes `/personas/reserve` to return synthetic, compliant personas; stores reservations in Postgres.
- **Contracts via Pact**: orchestrator is the consumer; data-broker verifies the contract.
- **Integration tests with Testcontainers**: Postgres and Redpanda (Kafka).
- **Resilience & fault tests**: Resilience4j timeouts/retries; `toxiproxy` to inject DB latency in a dedicated test.
- **WireMock**: example of HTTP dependency stubbing.
- **Perf smoke**: `k6` script to run a 60s baseline smoke on `data-broker`.
- **CI template**: GitHub Actions workflow showing contract-test then provider verification.

> Goal: get comfortable with Test Environment Mgmt, Test Data, Contract-First, and CI-first practices.

## Quick start
Requirements:
- JDK 21
- Maven 3.9+
- Docker + Docker Compose v2
- Node 18+ (only if you want to run k6 via `xk6`, otherwise use the official `grafana/k6` docker image)

### 1) Spin infra (Postgres, Kafka, Toxiproxy)
```bash
docker compose up -d
```

### 2) Run tests + build all modules
```bash
./mvnw -v  # if wrapper available; otherwise use: mvn -v
mvn -q -DskipTests clean package
mvn test
```

### 3) Run the services (two terminals)
```bash
# terminal 1
mvn -pl data-broker-service spring-boot:run
# terminal 2
mvn -pl orchestrator-service spring-boot:run
```

### 4) Try the flow
```bash
# Reserve a persona (data-broker)
curl -s http://localhost:8081/personas/reserve | jq

# Trigger a test run (orchestrator)
curl -s -X POST http://localhost:8080/run -H "Content-Type: application/json" -d '{"suite":"checkout","shards":2}'
```

### 5) Pact contracts
- Generate/verify consumer pact (from orchestrator):
```bash
mvn -pl orchestrator-service -Ppact test
```
- Verify on provider (data-broker):
```bash
mvn -pl data-broker-service -Ppact verify
```

### 6) Perf smoke on data-broker
```bash
docker run --rm -i --network host grafana/k6 run - < perf/k6/smoke.js
```

### 7) Simulate DB latency faults (toxiproxy) and run a fault-injection test
```bash
# add 1s latency to Postgres
docker exec toxiproxy-cli sh -c "toxiproxy-cli toxic add --upstream --type latency --toxicName pglatency --latency 1000 db"
mvn -pl data-broker-service -Pfaults test
# remove latency
docker exec toxiproxy-cli sh -c "toxiproxy-cli toxic rm pglatency db || true"
```

## Modules
- `data-broker-service` — Port 8081
- `orchestrator-service` — Port 8080

## CI (GitHub Actions)
The provided workflow runs unit tests, Pact consumer, then provider verification, and uploads artifacts.

---
**NOTE**: This is a sample project. Security and production hardening are intentionally simplified.

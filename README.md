# Test Platform Starter

A modern microservices test platform demonstrating various testing approaches and best practices for Spring Boot applications.

## Overview

This project consists of two microservices:
- **Data Broker Service**: Manages test data and personas
- **Orchestrator Service**: Orchestrates test execution and manages test runs

The platform demonstrates:
- Contract Testing with Pact
- Integration Testing with TestContainers
- Fault Injection Testing with ToxiProxy
- Performance Testing with k6
- Monitoring with Prometheus & Grafana

## Prerequisites

- Java 21
- Docker
- Maven

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/wise-test-platform-starter.git
cd wise-test-platform-starter
```

2. Start the infrastructure services:
```bash
docker compose up -d
```

3. Build and run the services:
```bash
./mvnw clean install
./mvnw spring-boot:run -pl data-broker-service
./mvnw spring-boot:run -pl orchestrator-service
```

## Service URLs

- Data Broker Service: http://localhost:8081
  - Swagger UI: http://localhost:8081/swagger-ui.html
  - API Docs: http://localhost:8081/v3/api-docs
  - Actuator: http://localhost:8081/actuator

- Orchestrator Service: http://localhost:8080
  - Swagger UI: http://localhost:8080/swagger-ui.html
  - API Docs: http://localhost:8080/v3/api-docs
  - Actuator: http://localhost:8080/actuator

- Monitoring
  - Prometheus: http://localhost:9090
  - Grafana: http://localhost:3000 (admin/admin)

## Features

### Security
- Basic authentication for API endpoints
- Secured actuator endpoints
- Environment variable configuration for credentials

### Monitoring & Observability
- Spring Boot Actuator integration
- Prometheus metrics collection
- Grafana dashboards for:
  - JVM metrics (memory, GC, threads)
  - Service metrics (request rate, latency, success rate)
  - Infrastructure metrics

### Testing
- Contract Testing (Pact)
  - Consumer-driven contract tests
  - Provider verification
- Integration Testing
  - TestContainers for PostgreSQL
  - WireMock for service virtualization
- Fault Injection
  - ToxiProxy for network fault simulation
- Performance Testing
  - k6 for load and performance testing

### CI/CD
- GitHub Actions workflow
- Maven dependency caching
- JaCoCo code coverage reporting
- Containerized builds

## Running Tests

### Unit & Integration Tests
```bash
./mvnw verify
```

### Contract Tests
Consumer tests:
```bash
./mvnw -pl orchestrator-service -Ppact test
```

Provider verification:
```bash
./mvnw -pl data-broker-service -Ppact verify
```

### Performance Tests
```bash
k6 run perf/k6/smoke.js
```

## Docker Support

Build the services:
```bash
docker build -t wise/data-broker-service:latest data-broker-service
docker build -t wise/orchestrator-service:latest orchestrator-service
```

## Configuration

### Environment Variables

Data Broker Service:
- `DB_HOST`: Database host (default: localhost)
- `DB_PORT`: Database port (default: 5432)
- `DB_NAME`: Database name (default: brokerdb)
- `DB_USER`: Database username (default: broker)
- `DB_PASSWORD`: Database password (default: broker)
- `ADMIN_USER`: Admin username (default: admin)
- `ADMIN_PASSWORD`: Admin password (default: admin)

Orchestrator Service:
- `DATA_BROKER_URL`: Data Broker Service URL (default: http://localhost:8081)
- `KAFKA_SERVERS`: Kafka bootstrap servers (default: localhost:9092)
- `ADMIN_USER`: Admin username (default: admin)
- `ADMIN_PASSWORD`: Admin password (default: admin)

## Contributing

1. Create a feature branch
2. Make your changes
3. Run tests and ensure CI passes
4. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

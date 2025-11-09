package com.example.orch.contracts;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.orch.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.Map;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "data-broker-service", port = "8081")
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles("test")
class OrchestratorPactConsumerTest {

    private WebClient webClient;

    @BeforeEach
    void setup() {
        webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    @DynamicPropertySource
    static void configurePactProperties(DynamicPropertyRegistry registry) {
        registry.add("data.broker.base-url", () -> "http://localhost:8081");
    }

    @Pact(consumer = "orchestrator-service", provider = "data-broker-service")
    public V4Pact createPersonaReservation(PactDslWithProvider builder) {
        return builder
                .given("A persona is available")
                .uponReceiving("A request to reserve a persona")
                .path("/api/personas/test-persona/reservations")
                .method("POST")
                .headers("Content-Type", "application/json")
                .body("{\"reservedBy\":\"test-user\",\"runId\":\"test-run-1\"}")
                .willRespondWith()
                .status(201)
                .headers(Map.of("Content-Type", "application/json"))
                .body("{\"id\":1,\"personaId\":\"test-persona\",\"reservedBy\":\"test-user\",\"runId\":\"test-run-1\"}")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createPersonaReservation")
    void shouldReservePersona() {
        StepVerifier.create(webClient.post()
                        .uri("/api/personas/test-persona/reservations")
                        .bodyValue(Map.of(
                                "reservedBy", "test-user",
                                "runId", "test-run-1"
                        ))
                        .retrieve()
                        .bodyToMono(String.class))
                .expectNextMatches(response -> response.contains("test-persona"))
                .verifyComplete();
    }

    @Pact(consumer = "orchestrator-service", provider = "data-broker-service")
    public V4Pact getReservationsForRun(PactDslWithProvider builder) {
        return builder
                .given("Reservations exist for a run")
                .uponReceiving("A request to get reservations for a run")
                .path("/api/personas/reservations")
                .method("GET")
                .query("runId=test-run-1")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("[{\"id\":1,\"personaId\":\"test-persona\",\"reservedBy\":\"test-user\",\"runId\":\"test-run-1\"}]")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getReservationsForRun")
    void shouldGetReservationsForRun() {
        StepVerifier.create(webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/personas/reservations")
                                .queryParam("runId", "test-run-1")
                                .build())
                        .retrieve()
                        .bodyToMono(String.class))
                .expectNextMatches(response -> response.contains("test-persona"))
                .verifyComplete();
    }
}

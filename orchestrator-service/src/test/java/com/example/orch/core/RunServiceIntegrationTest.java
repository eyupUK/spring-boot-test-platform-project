package com.example.orch.core;

import com.example.orch.config.TestConfig;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.test.StepVerifier;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles("test")
class RunServiceIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private RunService runService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("data.broker.base-url", () -> wireMock.baseUrl());
    }

    @Test
    void shouldCreateRunAndReservePersona() {
        // given
        wireMock.stubFor(post(urlEqualTo("/api/personas/test-persona/reservations"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"personaId\":\"test-persona\",\"reservedBy\":\"test-user\"}")));

        Map<String, String> runRequest = Map.of(
                "name", "Test Run",
                "description", "Test run with persona",
                "personaId", "test-persona"
        );

        // when
        StepVerifier.create(runService.createRun(runRequest))
                .expectNextMatches(run -> {
                    assertThat(run).containsKey("id");
                    assertThat(run).containsEntry("name", "Test Run");
                    assertThat(run).containsEntry("description", "Test run with persona");
                    return true;
                })
                .verifyComplete();

        // then
        wireMock.verify(postRequestedFor(urlEqualTo("/api/personas/test-persona/reservations"))
                .withHeader("Content-Type", containing("application/json")));
    }

    @Test
    void shouldHandlePersonaReservationFailure() {
        // given
        wireMock.stubFor(post(urlEqualTo("/api/personas/test-persona/reservations"))
                .willReturn(aResponse()
                        .withStatus(409)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Persona already reserved\"}")));

        Map<String, String> runRequest = Map.of(
                "name", "Test Run",
                "description", "Test run with persona",
                "personaId", "test-persona"
        );

        // when/then
        StepVerifier.create(runService.createRun(runRequest))
                .expectErrorMatches(throwable ->
                    throwable.getMessage().contains("Persona already reserved"))
                .verify();
    }

    @Test
    void shouldHandleNetworkFailure() {
        // given
        wireMock.stubFor(post(urlEqualTo("/api/personas/test-persona/reservations"))
                .willReturn(aResponse().withFixedDelay(5000))); // 5 second delay

        Map<String, String> runRequest = Map.of(
                "name", "Test Run",
                "description", "Test run with persona",
                "personaId", "test-persona"
        );

        // when/then
        StepVerifier.create(runService.createRun(runRequest))
                .expectError()
                .verify(java.time.Duration.ofSeconds(10));
    }

    @Test
    void shouldGetExistingRun() {
        // given
        String runId = "test-run-1";
        wireMock.stubFor(get(urlPathEqualTo("/api/personas/reservations"))
                .withQueryParam("runId", equalTo(runId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":1,\"personaId\":\"test-persona\",\"runId\":\"test-run-1\"}]")));

        // when/then
        StepVerifier.create(runService.getRun(runId))
                .expectNextMatches(run -> {
                    assertThat(run).containsKey("id");
                    assertThat(run).containsKey("personaReservations");
                    return true;
                })
                .verifyComplete();
    }
}

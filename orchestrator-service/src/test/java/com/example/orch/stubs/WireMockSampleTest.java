package com.example.orch.stubs;

import com.example.orch.config.TestConfig;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
class WireMockSampleTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private WebClient webClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("data.broker.base-url", () -> wireMock.baseUrl());
    }

    @Test
    void shouldHandleSuccessfulResponse() {
        // given
        wireMock.stubFor(post(urlEqualTo("/api/personas/test-persona/reservations"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"personaId\":\"test-persona\",\"reservedBy\":\"test-user\"}")
                        .withStatus(201)));

        // when
        Mono<String> response = webClient.post()
                .uri(wireMock.baseUrl() + "/api/personas/test-persona/reservations")
                .bodyValue("{\"reservedBy\":\"test-user\"}")
                .retrieve()
                .bodyToMono(String.class);

        // then
        StepVerifier.create(response)
                .expectNextMatches(body -> body.contains("test-persona"))
                .verifyComplete();
    }

    @Test
    void shouldHandleErrorResponse() {
        // given
        wireMock.stubFor(post(urlEqualTo("/api/personas/test-persona/reservations"))
                .willReturn(aResponse()
                        .withStatus(409)
                        .withBody("{\"error\":\"Persona already reserved\"}")));

        // when
        Mono<String> response = webClient.post()
                .uri(wireMock.baseUrl() + "/api/personas/test-persona/reservations")
                .bodyValue("{\"reservedBy\":\"test-user\"}")
                .retrieve()
                .bodyToMono(String.class);

        // then
        StepVerifier.create(response)
                .expectError()
                .verify();
    }

    @Test
    void shouldHandleNetworkError() {
        // given
        wireMock.stubFor(post(urlEqualTo("/api/personas/test-persona/reservations"))
                .willReturn(aResponse().withFixedDelay(5000))); // 5 second delay

        // when
        Mono<String> response = webClient.post()
                .uri(wireMock.baseUrl() + "/api/personas/test-persona/reservations")
                .bodyValue("{\"reservedBy\":\"test-user\"}")
                .retrieve()
                .bodyToMono(String.class);

        // then
        StepVerifier.create(response)
                .expectError()
                .verify();
    }
}

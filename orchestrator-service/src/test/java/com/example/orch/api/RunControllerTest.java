package com.example.orch.api;

import com.example.orch.core.RunService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(RunController.class)
class RunControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RunService runService;

    @Test
    @WithMockUser
    void shouldCreateRun() {
        // given
        Map<String, String> run = Map.of(
                "id", "test-run-1",
                "name", "Test Run",
                "description", "Test Description"
        );

        when(runService.createRun(any())).thenReturn(Mono.just(run));

        // when/then
        webTestClient.post()
                .uri("/api/runs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "name", "Test Run",
                        "description", "Test Description"
                ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("test-run-1")
                .jsonPath("$.name").isEqualTo("Test Run")
                .jsonPath("$.description").isEqualTo("Test Description");
    }

    @Test
    @WithMockUser
    void shouldGetRun() {
        // given
        Map<String, String> run = Map.of(
                "id", "test-run-1",
                "name", "Test Run",
                "description", "Test Description"
        );

        when(runService.getRun("test-run-1")).thenReturn(Mono.just(run));

        // when/then
        webTestClient.get()
                .uri("/api/runs/test-run-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("test-run-1")
                .jsonPath("$.name").isEqualTo("Test Run")
                .jsonPath("$.description").isEqualTo("Test Description");
    }

    @Test
    @WithMockUser
    void shouldHandleNotFoundRun() {
        // given
        when(runService.getRun("non-existent")).thenReturn(Mono.empty());

        // when/then
        webTestClient.get()
                .uri("/api/runs/non-existent")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldRequireAuthentication() {
        webTestClient.get()
                .uri("/api/runs/test-run-1")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}

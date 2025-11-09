package com.example.orch.core;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RunService {

  private final WebClient webClient;
  private final ConcurrentHashMap<String, Map<String, Object>> runStore = new ConcurrentHashMap<>();

  @Value("${data.broker.base-url:http://localhost:8081}")
  private String dataBrokerBaseUrl;

  public Mono<Map> planAndRun(String suite, int shards) {
    // 1) fetch persona from data-broker
    Mono<Map> persona = webClient.get()
        .uri(dataBrokerBaseUrl + "/personas/reserve")
        .retrieve().bodyToMono(Map.class);

    // 2) construct a tiny DAG (demo) and return plan
    return persona.map(p -> Map.of(
        "suite", suite,
        "shards", shards,
        "persona", p,
        "steps", new String[]{"contracts", "integration", "e2e-slim"}
    ));
  }

  public Mono<Map<String, Object>> createRun(Map<String, String> request) {
    String runId = UUID.randomUUID().toString();
    String personaId = request.get("personaId");

    if (personaId == null) {
      Map<String, Object> run = new HashMap<>(request);
      run.put("id", runId);
      runStore.put(runId, run);
      return Mono.just(run);
    }

    // Reserve persona from data broker
    return webClient.post()
        .uri(dataBrokerBaseUrl + "/api/personas/" + personaId + "/reservations")
        .bodyValue(Map.of(
            "runId", runId,
            "reservedBy", request.getOrDefault("createdBy", "system")
        ))
        .retrieve()
        .onStatus(status -> status.value() == 409,
            response -> Mono.error(new RuntimeException("Persona already reserved")))
        .bodyToMono(Map.class)
        .timeout(java.time.Duration.ofSeconds(3))
        .map(reservation -> {
          Map<String, Object> run = new HashMap<>(request);
          run.put("id", runId);
          run.put("personaReservation", reservation);
          runStore.put(runId, run);
          return run;
        });
  }

  public Mono<Map<String, Object>> getRun(String runId) {
    Map<String, Object> run = runStore.get(runId);
    if (run != null) {
      return Mono.just(run);
    }

    // Try to fetch from data broker
    return webClient.get()
        .uri(dataBrokerBaseUrl + "/api/personas/reservations?runId=" + runId)
        .retrieve()
        .bodyToMono(Map[].class)
        .map(reservations -> {
          Map<String, Object> result = new HashMap<>();
          result.put("id", runId);
          result.put("personaReservations", reservations);
          return result;
        });
  }
}

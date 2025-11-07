package com.example.orch.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RunService {

  private final WebClient client = WebClient.builder().baseUrl("http://localhost:8081").build();

  public Mono<Map> planAndRun(String suite, int shards) {
    // 1) fetch persona from data-broker
    Mono<Map> persona = client.get().uri("/personas/reserve")
        .retrieve().bodyToMono(Map.class);

    // 2) construct a tiny DAG (demo) and return plan
    return persona.map(p -> Map.of(
        "suite", suite,
        "shards", shards,
        "persona", p,
        "steps", new String[]{"contracts", "integration", "e2e-slim"}
    ));
  }
}

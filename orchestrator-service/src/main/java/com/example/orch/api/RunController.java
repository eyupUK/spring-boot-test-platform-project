package com.example.orch.api;

import com.example.orch.core.RunService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RunController {

  private final RunService svc;

  @PostMapping(value = "/run", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Map> run(@RequestBody RunRequest req) {
    return svc.planAndRun(req.suite, req.shards <= 0 ? 1 : req.shards);
  }

  @PostMapping(value = "/api/runs", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Map<String, Object>> createRun(@RequestBody Map<String, String> request) {
    return svc.createRun(request);
  }

  @GetMapping("/api/runs/{id}")
  public Mono<ResponseEntity<Map<String, Object>>> getRun(@PathVariable String id) {
    return svc.getRun(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @Data
  static class RunRequest {
    public String suite;
    public int shards;
  }
}

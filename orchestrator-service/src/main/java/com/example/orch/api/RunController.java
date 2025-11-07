package com.example.orch.api;

import com.example.orch.core.RunService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

  @Data
  static class RunRequest {
    public String suite;
    public int shards;
  }
}

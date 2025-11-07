package com.example.broker.faults;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Placeholder for a toxiproxy-driven latency test.
 * See README for the docker exec commands to inject latency, then enable and run this test.
 */
public class LatencyInjectedTest {
  @Test @Disabled("Enable after adding latency with toxiproxy")
  void underLatency() {
    // perform a simple repository call and assert timeouts/retries behaviour
    // kept minimal for brevity
  }
}

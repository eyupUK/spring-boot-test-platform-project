package com.example.orch.contracts;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "data-broker-service", port = "0")
public class OrchestratorPactConsumerTest {

  @Pact(consumer = "orchestrator-service")
  public V4Pact createPact(PactDslWithProvider builder) {
    return builder
      .uponReceiving("reserve persona")
      .path("/personas/reserve").method("GET")
      .willRespondWith().status(200)
      .headers(Map.of("Content-Type","application/json"))
      .body("{\"id\":1,\"email\":\"user@example.com\",\"name\":\"Test User\",\"country\":\"GB\"}")
      .toPact(V4Pact.class);
  }

  @Test
  void verify(MockServer server) {
    given().baseUri(server.getUrl())
      .when().get("/personas/reserve")
      .then().statusCode(200)
      .body("email", not(isEmptyString()))
      .body("country", not(isEmptyString()));
  }
}

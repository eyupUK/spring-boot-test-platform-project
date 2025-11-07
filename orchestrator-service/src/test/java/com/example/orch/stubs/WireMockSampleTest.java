package com.example.orch.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockSampleTest {

  static WireMockServer wm = new WireMockServer(0);

  @BeforeAll static void before() {
    wm.start();
    configureFor("localhost", wm.port());
    stubFor(get(urlEqualTo("/health")).willReturn(aResponse().withStatus(200).withBody("OK")));
  }

  @AfterAll static void after(){ wm.stop(); }

  @Test void stubWorks() {
    // placeholder: call wm.baseUrl() + "/health" with any HTTP client as needed
  }
}

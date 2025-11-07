package com.example.orch;

import com.example.orch.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles("test")
class OrchestratorApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void applicationPropertiesAreLoaded() {
        String serverPort = applicationContext.getEnvironment().getProperty("server.port");
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        String dataBrokerUrl = applicationContext.getEnvironment().getProperty("data.broker.base-url");

        assertThat(serverPort).isEqualTo("8080");
        assertThat(applicationName).isEqualTo("orchestrator-service");
        assertThat(dataBrokerUrl).isNotNull();
    }
}

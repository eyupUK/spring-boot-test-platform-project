package com.example.broker;

import com.example.broker.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
class DataBrokerApplicationTests {

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

        assertThat(serverPort).isEqualTo("8081");
        assertThat(applicationName).isEqualTo("data-broker-service");
    }
}

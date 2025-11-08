package com.example.broker;

import com.example.broker.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {DataBrokerApplication.class, TestConfig.class})
@ActiveProfiles("test")
class SimpleConfigurationTest {

    @Test
    void contextLoads() {
        // This test will fail if the application context cannot be created
    }
}

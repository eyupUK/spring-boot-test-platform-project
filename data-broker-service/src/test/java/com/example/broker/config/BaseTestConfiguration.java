package com.example.broker.config;

import com.example.broker.ContainersConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({ContainersConfig.class, SecurityTestConfig.class})
public abstract class BaseTestConfiguration {
    // This class serves as a base for all tests, importing common configurations
}

package com.vamshi.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: verifies the Spring application context loads without errors.
 *
 * Kafka, Redis, and Mail auto-configuration are disabled via the test profile
 * in application.yml so this test runs without Docker. Full integration tests
 * using Testcontainers are added in later phases alongside the features they test.
 */
@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}

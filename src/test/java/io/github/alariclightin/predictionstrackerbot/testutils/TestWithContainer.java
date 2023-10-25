package io.github.alariclightin.predictionstrackerbot.testutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class TestWithContainer {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.9-alpine");

    static {
        postgres.start();
    }

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    protected void clearAllTables() {
        jdbcTemplate.execute(
            """
                TRUNCATE TABLE 
                    predictions.questions, 
                    predictions.predictions, 
                    predictions.reminders,
                    
                    users.user_settings
            """);
    }
}

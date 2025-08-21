package com.palakendra.palakendra.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

@TestConfiguration
public class FlywayH2TestConfig {

    @Bean
    @Order(0)
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                // Load shared + H2-specific migrations explicitly
                .locations("classpath:db/migration/common", "classpath:db/migration/h2")
                .baselineOnMigrate(true)
                .cleanDisabled(false)
                .load();
        flyway.clean();   // ensure fresh schema for every test run
        flyway.migrate(); // apply all migrations
        return flyway;
    }
}

package com.palakendra.palakendra.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("repair") // only runs when you enable the 'repair' profile
public class FlywayRepairRunner implements CommandLineRunner {
    private final Flyway flyway;
    public FlywayRepairRunner(Flyway flyway) { this.flyway = flyway; }

    @Override
    public void run(String... args) {
        flyway.repair();
        System.out.println("✅ Flyway repair completed.");
    }
}

package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.support.StartupService;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StartupStrategy implements FlywayMigrationStrategy {

    private final Logger logger = LoggerFactory.getLogger(StartupStrategy.class);

    private final ApplicationContext applicationContext;

    @Autowired
    public StartupStrategy(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void migrate(Flyway flyway) {
        // Migrating the database
        logger.info("Migrating the database...");
        flyway.migrate();
        // Startup services
        logger.info("Starting startup services...");
        applicationContext.getBeansOfType(StartupService.class).values().forEach(startupService -> {
            logger.info("Starting {}", startupService.getName());
            startupService.start();
        });
    }
}

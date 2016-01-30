package net.nemerosa.ontrack.repository.config;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.StartupService;
import net.nemerosa.ontrack.repository.support.DBInitConfig;
import net.sf.dbinit.DBInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creation or update of the database.
 */
@Component
public class DBStartup {

    private final Logger logger = LoggerFactory.getLogger(DBStartup.class);

    private final List<DBInitConfig> dbInitConfigs;
    private final List<StartupService> startupServices;
    private final SecurityService securityService;

    @Autowired
    public DBStartup(List<DBInitConfig> dbInitConfigs, List<StartupService> startupServices, SecurityService securityService) {
        this.securityService = securityService;
        // Sorts the DB configurations
        this.dbInitConfigs = new ArrayList<>(dbInitConfigs);
        Collections.sort(
                this.dbInitConfigs,
                (o1, o2) -> o2.getOrder() - o1.getOrder()
        );
        // Sorts the startup services
        List<StartupService> services = new ArrayList<>(startupServices);
        Collections.sort(services, (o1, o2) -> o1.startupOrder() - o2.startupOrder());
        this.startupServices = services;
    }

    /**
     * Runs all database configurations and runs each {@link StartupService} in turn.
     */
    @PostConstruct
    public void init() {

        securityService.asAdmin(() -> {

            logger.info("[db] DB initialisation.");
            for (DBInitConfig dbInitConfig : dbInitConfigs) {
                logger.info("[db] DB initialisation for \"{}\"", dbInitConfig.getName());
                DBInit dbInit = dbInitConfig.createConfig();
                dbInit.run();
            }

            logger.info("[startup] Running startup services");
            for (StartupService startupService : startupServices) {
                logger.info("[startup] Starting service \"{}\"", startupService.getName());
                startupService.start();
            }

        });

    }
}

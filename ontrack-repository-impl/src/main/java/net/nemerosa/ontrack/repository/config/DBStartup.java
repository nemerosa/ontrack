package net.nemerosa.ontrack.repository.config;

import net.nemerosa.ontrack.model.support.StartupService;
import net.nemerosa.ontrack.repository.support.DBInitConfig;
import net.sf.dbinit.DBInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Creation or update of the database.
 */
@Component
public class DBStartup {

    private final Logger logger = LoggerFactory.getLogger(DBStartup.class);

    private final List<DBInitConfig> dbInitConfigs;
    private final List<StartupService> startupServices;

    @Autowired
    public DBStartup(List<DBInitConfig> dbInitConfigs, List<StartupService> startupServices) {
        // Sorts the DB configurations
        this.dbInitConfigs = new ArrayList<>(dbInitConfigs);
        this.dbInitConfigs.sort((o1, o2) -> o2.getOrder() - o1.getOrder());
        // Sorts the startup services
        List<StartupService> services = new ArrayList<>(startupServices);
        services.sort(Comparator.comparingInt(StartupService::startupOrder));
        this.startupServices = services;
    }

    /**
     * Runs all database configurations and runs each {@link StartupService} in turn.
     */
    @PostConstruct
    public void init() {

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

    }
}

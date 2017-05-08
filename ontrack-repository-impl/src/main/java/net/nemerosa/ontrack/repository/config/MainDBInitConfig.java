package net.nemerosa.ontrack.repository.config;

import net.nemerosa.ontrack.model.support.DBMigrationAction;
import net.nemerosa.ontrack.repository.support.AbstractDBInitConfig;
import net.nemerosa.ontrack.repository.support.ConfiguredDBInit;
import net.nemerosa.ontrack.repository.support.DBMigrationPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MainDBInitConfig extends AbstractDBInitConfig {

    public static final int VERSION = 38;

    private final ApplicationContext applicationContext;

    @Autowired
    public MainDBInitConfig(DataSource dataSource, ApplicationContext applicationContext) {
        super(dataSource);
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String getName() {
        return "main";
    }

    @Override
    public ConfiguredDBInit createConfig() {
        ConfiguredDBInit db = new ConfiguredDBInit();
        db.setVersion(VERSION);
        db.setJdbcDataSource(dataSource);
        db.setVersionTable("ONTRACK_VERSION");
        db.setVersionColumnName("VALUE");
        db.setVersionColumnTimestamp("UPDATED");
        db.setResourceInitialization("/META-INF/db/init.sql");
        db.setResourceUpdate("/META-INF/db/update.{0}.sql");

        // Gets the migration actions
        List<DBMigrationPatch> migrationPatches = applicationContext.getBeansOfType(DBMigrationAction.class).values().stream()
                .map(migrationAction -> new DBMigrationPatch(migrationAction.getPatch(), migrationAction))
                .collect(Collectors.toList());

        db.setPatchActions(migrationPatches);

        // OK
        return db;
    }

}

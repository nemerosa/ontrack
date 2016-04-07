package net.nemerosa.ontrack.migration.postgresql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

@Component
public class Migration {

    private final Logger logger = LoggerFactory.getLogger(Migration.class);

    private final MigrationProperties migrationProperties;
    private final NamedParameterJdbcTemplate h2;
    private final NamedParameterJdbcTemplate postgresql;
    private final TransactionTemplate txTemplate;

    @Autowired
    public Migration(@Qualifier("h2") DataSource h2Datasource, @Qualifier("postgresql") DataSource postgresqlDatasource, MigrationProperties migrationProperties) {
        this.migrationProperties = migrationProperties;
        h2 = new NamedParameterJdbcTemplate(h2Datasource);
        postgresql = new NamedParameterJdbcTemplate(postgresqlDatasource);

        PlatformTransactionManager txManager = new DataSourceTransactionManager(postgresqlDatasource);
        txTemplate = new TransactionTemplate(txManager);
    }

    public void run() {
        // Cleanup?
        if (migrationProperties.isCleanup()) {
            cleanup();
        }
        // PROJECTS
        tx(() -> simpleMigration(
                "Projects",
                "SELECT * FROM PROJECTS",
                Collections.emptyMap(),
                "INSERT INTO PROJECTS (ID, NAME, DESCRIPTION, DISABLED) VALUES (:ID, :NAME, :DESCRIPTION, :DISABLED)"
        ));

        // BRANCHES
        tx(() -> simpleMigration(
                "Branches",
                "SELECT * FROM BRANCHES",
                Collections.emptyMap(),
                "INSERT INTO BRANCHES (ID, PROJECTID, NAME, DESCRIPTION, DISABLED) VALUES (:ID, :PROJECTID, :NAME, :DESCRIPTION, :DISABLED)"
        ));

        // TODO ACCOUNTS
        // TODO ACCOUNT_GROUPS
        // TODO ACCOUNT_GROUP_LINK
        // TODO ACCOUNT_GROUP_MAPPING
        // TODO BRANCH_TEMPLATE_DEFINITIONS
        // TODO BRANCH_TEMPLATE_DEFINITION_PARAMS
        // TODO BRANCH_TEMPLATE_INSTANCES
        // TODO BRANCH_TEMPLATE_INSTANCE_PARAMS
        // TODO BUILDS
        // TODO BUILD_FILTERS
        // TODO CONFIGURATIONS
        // TODO ENTITY_DATA
        // TODO EVENTS
        // TODO EXT_SVN_COPY
        // TODO EXT_SVN_MERGE_REVISION
        // TODO EXT_SVN_REPOSITORY
        // TODO EXT_SVN_REVISION
        // TODO EXT_SVN_REVISION_ISSUE
        // TODO EXT_SVN_STOP
        // TODO EXT_SVN_VERSION
        // TODO GLOBAL_AUTHORIZATIONS
        // TODO GROUP_GLOBAL_AUTHORIZATIONS
        // TODO GROUP_PROJECT_AUTHORIZATIONS
        // TODO ONTRACK_VERSION
        // TODO PREDEFINED_PROMOTION_LEVELS
        // TODO PREDEFINED_VALIDATION_STAMPS
        // TODO PREFERENCES
        // TODO PROJECT_AUTHORIZATIONS
        // TODO PROMOTION_LEVELS
        // TODO PROMOTION_RUNS
        // TODO PROPERTIES
        // TODO SETTINGS
        // TODO SHARED_BUILD_FILTERS
        // TODO STORAGE
        // TODO VALIDATION_RUNS
        // TODO VALIDATION_RUN_STATUSES
        // TODO VALIDATION_STAMPS

    }

    private void cleanup() {
        logger.info("Cleanup of target database...");
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                postgresql.update("DELETE FROM PROJECTS", Collections.emptyMap());
            }
        });
    }

    private void tx(Runnable task) {
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                task.run();
            }
        });
    }

    private void simpleMigration(String name, String h2Query, Map<String, Object> h2Params, String postgresqlUpdate) {
        logger.info("Migrating {}...", name);
        long count = h2.queryForList(h2Query, h2Params)
                .stream()
                .map(it -> postgresql.update(postgresqlUpdate, it))
                .count();
        logger.info("{} count = {}...", name, count);
    }

}

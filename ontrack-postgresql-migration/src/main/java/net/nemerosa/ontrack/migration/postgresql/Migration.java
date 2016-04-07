package net.nemerosa.ontrack.migration.postgresql;

import org.apache.commons.lang3.StringUtils;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

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

        /**
         * Entities
         */

        // PROJECTS
        copy("PROJECTS", "ID", "NAME", "DESCRIPTION", "DISABLED");

        // BRANCHES
        copy("BRANCHES", "ID", "PROJECTID", "NAME", "DESCRIPTION", "DISABLED");

        // PROMOTION_LEVELS
        copy("PROMOTION_LEVELS", "ID", "BRANCHID", "ORDERNB", "NAME", "DESCRIPTION", "IMAGETYPE", "IMAGEBYTES");

        // VALIDATION_STAMPS
        copy("VALIDATION_STAMPS", "ID", "BRANCHID", "OWNER", "PROMOTION_LEVEL", "ORDERNB", "NAME", "DESCRIPTION", "IMAGETYPE", "IMAGEBYTES");

        // BUILDS
        copy("BUILDS", "ID", "BRANCHID", "NAME", "DESCRIPTION", "CREATION", "CREATOR");

        // PROMOTION_RUNS
        copy("PROMOTION_RUNS", "ID", "BUILDID", "PROMOTIONLEVELID", "CREATION", "CREATOR", "DESCRIPTION");

        // VALIDATION_RUNS
        copy("VALIDATION_RUNS", "ID", "BUILDID", "VALIDATIONSTAMPID");

        // VALIDATION_RUN_STATUSES
        copy("VALIDATION_RUN_STATUSES", "ID", "VALIDATIONRUNID", "VALIDATIONRUNSTATUSID", "CREATION", "CREATOR", "DESCRIPTION");

        /**
         * Configurations
         */

        // CONFIGURATIONS
        // TODO JSON
        copy("CONFIGURATIONS", "ID", "TYPE", "NAME", "CONTENT");

        // SETTINGS
        copy("SETTINGS", "CATEGORY", "NAME", "VALUE");

        /**
         * Entity data
         */

        // ENTITY_DATA
        copy("ENTITY_DATA", "ID", "PROJECT", "BRANCH", "PROMOTION_LEVEL", "VALIDATION_STAMP", "BUILD", "PROMOTION_RUN", "VALIDATION_RUN", "NAME", "VALUE");

        // PROPERTIES
        // TODO JSON
        copy("PROPERTIES", "ID", "PROJECT", "BRANCH", "PROMOTION_LEVEL", "VALIDATION_STAMP", "BUILD", "PROMOTION_RUN", "VALIDATION_RUN", "TYPE", "SEARCHKEY", "JSON");

        /**
         * ACL
         */

        // ACCOUNTS
        copy("ACCOUNTS", "ID", "NAME", "FULLNAME", "EMAIL", "MODE", "PASSWORD", "ROLE");

        // ACCOUNT_GROUPS
        copy("ACCOUNT_GROUPS", "ID", "NAME", "DESCRIPTION");

        // ACCOUNT_GROUP_LINK
        copy("ACCOUNT_GROUP_LINK", "ACCOUNT", "ACCOUNTGROUP");

        // ACCOUNT_GROUP_MAPPING
        copy("ACCOUNT_GROUP_MAPPING", "ID", "GROUPID", "MAPPING", "SOURCE");

        // TODO BRANCH_TEMPLATE_DEFINITIONS
        // TODO BRANCH_TEMPLATE_DEFINITION_PARAMS
        // TODO BRANCH_TEMPLATE_INSTANCES
        // TODO BRANCH_TEMPLATE_INSTANCE_PARAMS
        // TODO BUILD_FILTERS
        // TODO EVENTS
        // TODO GLOBAL_AUTHORIZATIONS
        // TODO GROUP_GLOBAL_AUTHORIZATIONS
        // TODO GROUP_PROJECT_AUTHORIZATIONS
        // TODO PREDEFINED_PROMOTION_LEVELS
        // TODO PREDEFINED_VALIDATION_STAMPS
        // TODO PREFERENCES
        // TODO PROJECT_AUTHORIZATIONS
        // TODO SHARED_BUILD_FILTERS
        // TODO STORAGE

        // Subversion
        // Subversion tables do not need to be migrated - they will be filled on demand

        // Update of sequences
        updateSequences();

    }

    private void updateSequences() {
        logger.info("Resetting the sequences...");
        String[] tables = {
                "ACCOUNT_GROUP_MAPPING",
                "ACCOUNT_GROUPS",
                "ACCOUNTS",
                "BRANCHES",
                "BUILDS",
                "CONFIGURATIONS",
                "ENTITY_DATA",
                "EVENTS",
                "EXT_SVN_REPOSITORY",
                "PREDEFINED_PROMOTION_LEVELS",
                "PREDEFINED_VALIDATION_STAMPS",
                "PROJECTS",
                "PROMOTION_LEVELS",
                "PROMOTION_RUNS",
                "PROPERTIES",
                "VALIDATION_RUN_STATUSES",
                "VALIDATION_RUNS",
                "VALIDATION_STAMPS",
        };
        tx(() -> Arrays.asList(tables).stream().forEach(table -> {
            Integer max = postgresql.queryForObject(
                    String.format("SELECT MAX(ID) AS ID FROM %s", table),
                    Collections.emptyMap(),
                    Integer.class
            );
            int value = max != null ? max + 1 : 1;
            postgresql.update(
                    String.format("ALTER SEQUENCE %s_ID_SEQ RESTART WITH %d", table, value),
                    Collections.emptyMap()
            );
            logger.info("Resetting sequence for {} to {}.", table, value);
        }));
    }

    private void cleanup() {
        logger.info("Cleanup of target database...");
        String[] tables = {
                "ACCOUNTS",
                "ACCOUNT_GROUPS",
                "CONFIGURATIONS",
                "EXT_SVN_REPOSITORY",
                "PREDEFINED_PROMOTION_LEVELS",
                "PREDEFINED_VALIDATION_STAMPS",
                "PROJECTS",
                "SETTINGS",
                "STORAGE",
        };
        tx(() -> {
            for (String table : tables) {
                postgresql.update(String.format("DELETE FROM %s", table), Collections.emptyMap());
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

    private void copy(String table, String... columns) {
        String h2Query = String.format("SELECT * FROM %s", table);

        String insert = StringUtils.join(columns, ",");
        String values = Arrays.asList(columns).stream().map(column -> ":" + column).collect(Collectors.joining(","));
        String postgresqlUpdate = String.format("INSERT INTO %s (%s) VALUES (%s)", table, insert, values);

        tx(() -> simpleMigration(
                table,
                h2Query,
                Collections.emptyMap(),
                postgresqlUpdate
        ));
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

package net.nemerosa.ontrack.migration.postgresql;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.PreparedStatementCallback;
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
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;

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

        /*
         * Global data
         */

        // CONFIGURATIONS
        copy("CONFIGURATIONS", "ID", "TYPE", "NAME", "CONTENT::JSONB");

        // SETTINGS
        copy("SETTINGS", "CATEGORY", "NAME", "VALUE");

        // PREDEFINED_PROMOTION_LEVELS
        copy("PREDEFINED_PROMOTION_LEVELS", "ID", "ORDERNB", "NAME", "DESCRIPTION", "IMAGETYPE", "IMAGEBYTES");

        // PREDEFINED_VALIDATION_STAMPS
        copy("PREDEFINED_VALIDATION_STAMPS", "ID", "NAME", "DESCRIPTION", "IMAGETYPE", "IMAGEBYTES");

        // STORAGE
        copy("STORAGE", "STORE", "NAME", "DATA::JSONB");

        /*
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

        /*
         * Branch templating
         */

        // BRANCH_TEMPLATE_DEFINITIONS
        copy("BRANCH_TEMPLATE_DEFINITIONS", "BRANCHID", "ABSENCEPOLICY", "SYNCINTERVAL", "SYNCHRONISATIONSOURCEID", "SYNCHRONISATIONSOURCECONFIG::JSONB");

        // BRANCH_TEMPLATE_DEFINITION_PARAMS
        copy("BRANCH_TEMPLATE_DEFINITION_PARAMS", "BRANCHID", "NAME", "DESCRIPTION", "EXPRESSION");

        // BRANCH_TEMPLATE_INSTANCES
        copy("BRANCH_TEMPLATE_INSTANCES", "BRANCHID", "TEMPLATEBRANCHID");

        // BRANCH_TEMPLATE_INSTANCE_PARAMS
        copy("BRANCH_TEMPLATE_INSTANCE_PARAMS", "BRANCHID", "NAME", "VALUE");

        /*
         * Entity data
         */

        // ENTITY_DATA
        copy("ENTITY_DATA", "ID", "PROJECT", "BRANCH", "PROMOTION_LEVEL", "VALIDATION_STAMP", "BUILD", "PROMOTION_RUN", "VALIDATION_RUN", "NAME", "VALUE::JSONB");

        // PROPERTIES
        copyProperties();

        // SHARED_BUILD_FILTERS
        copy("SHARED_BUILD_FILTERS", "BRANCHID", "NAME", "TYPE", "DATA::JSONB");

        // BUILD_LINKS
        copy("BUILD_LINKS", "ID", "BUILDID", "TARGETBUILDID");

        // EVENTS
        if (migrationProperties.isSkipEvents()) {
            logger.warn("Skipping events migration");
        } else {
            copyEvents();
        }

        /*
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

        // GLOBAL_AUTHORIZATIONS
        copy("GLOBAL_AUTHORIZATIONS", "ACCOUNT", "ROLE");

        // GROUP_GLOBAL_AUTHORIZATIONS
        copy("GROUP_GLOBAL_AUTHORIZATIONS", "ACCOUNTGROUP", "ROLE");

        // GROUP_PROJECT_AUTHORIZATIONS
        copy("GROUP_PROJECT_AUTHORIZATIONS", "ACCOUNTGROUP", "PROJECT", "ROLE");

        // PROJECT_AUTHORIZATIONS
        copy("PROJECT_AUTHORIZATIONS", "ACCOUNT", "PROJECT", "ROLE");

        // PREFERENCES
        copy("PREFERENCES", "ACCOUNTID", "TYPE", "CONTENT");

        // BUILD_FILTERS
        copy("BUILD_FILTERS", "ACCOUNTID", "BRANCHID", "NAME", "TYPE", "DATA::JSONB");

        // PROJECT_FAVOURITES
        copy("PROJECT_FAVOURITES", "ID", "ACCOUNTID", "PROJECTID");

        // Subversion
        // Subversion tables do not need to be migrated - they will be filled on demand

        // Update of sequences
        updateSequences();

    }

    private void copyProperties() {
        copyWithTmp(
                "PROPERTIES",
                "CREATE TABLE TMP_PROPERTIES " +
                        "( " +
                        "  ID INTEGER PRIMARY KEY, " +
                        "  TYPE CHARACTER VARYING(150), " +
                        "  PROJECT INTEGER, " +
                        "  BRANCH INTEGER, " +
                        "  PROMOTION_LEVEL INTEGER, " +
                        "  VALIDATION_STAMP INTEGER, " +
                        "  BUILD INTEGER, " +
                        "  PROMOTION_RUN INTEGER, " +
                        "  VALIDATION_RUN INTEGER, " +
                        "  SEARCHKEY CHARACTER VARYING(200), " +
                        "  JSON JSONB " +
                        ");",
                "ID", "PROJECT", "BRANCH", "PROMOTION_LEVEL", "VALIDATION_STAMP", "BUILD", "PROMOTION_RUN", "VALIDATION_RUN", "TYPE", "SEARCHKEY", "JSON::JSONB"
        );
    }

    private void copyEvents() {
        copyWithTmp(
                "EVENTS",
                "CREATE TABLE TMP_EVENTS " +
                        "( " +
                        "  ID INTEGER PRIMARY KEY, " +
                        "  EVENT_TYPE CHARACTER VARYING(120), " +
                        "  PROJECT INTEGER, " +
                        "  BRANCH INTEGER, " +
                        "  PROMOTION_LEVEL INTEGER, " +
                        "  VALIDATION_STAMP INTEGER, " +
                        "  BUILD INTEGER, " +
                        "  PROMOTION_RUN INTEGER, " +
                        "  VALIDATION_RUN INTEGER, " +
                        "  REF CHARACTER VARYING(20), " +
                        "  EVENT_VALUES CHARACTER VARYING(500), " +
                        "  EVENT_TIME CHARACTER VARYING(24), " +
                        "  EVENT_USER CHARACTER VARYING(40) " +
                        ");",
                "ID", "PROJECT", "BRANCH", "PROMOTION_LEVEL", "VALIDATION_STAMP", "BUILD", "PROMOTION_RUN", "VALIDATION_RUN", "EVENT_TYPE", "REF", "EVENT_VALUES", "EVENT_TIME", "EVENT_USER"
        );
    }

    private void copyWithTmp(String table, String tmpCreation, String... columns) {
        String h2Query = format("SELECT * FROM %s", table);

        String insert = Arrays.stream(columns).map(column -> StringUtils.substringBefore(column, "::")).collect(Collectors.joining(","));
        String postgresqlUpdate = format(
                "INSERT INTO TMP_%s (%s) VALUES (%s)",
                table,
                insert,
                Arrays.stream(columns)
                        .map(column -> "?" + (StringUtils.contains(column, "::") ? "::" + StringUtils.substringAfter(column, "::") : ""))
                        .collect(Collectors.joining(","))
        );

        tx(() -> {
            // Makes sure temp table is dropped
            postgresql.getJdbcOperations().execute(format("DROP TABLE IF EXISTS TMP_%s", table));
            // Creates temporary table, without any contraint
            logger.info(format("Creating TMP_%s...", table));
            postgresql.getJdbcOperations().execute(tmpCreation);
        });

        int count = intx(() -> {
            logger.info("Migrating {} to TMP_{} (no check)...", table, table);
            List<Map<String, Object>> sources = h2.queryForList(h2Query, Collections.emptyMap());
            int size = sources.size();

            postgresql.getJdbcOperations().execute(
                    postgresqlUpdate,
                    (PreparedStatementCallback<Void>) ps -> {
                        int index = 0;
                        int tosend = 0;
                        for (Map<String, Object> source : sources) {
                            index++;
                            int i = 1;
                            for (String column : columns) {
                                ps.setObject(i++, source.get(StringUtils.substringBefore(column, "::")));
                            }
                            ps.addBatch();
                            tosend++;
                            if (tosend % 1000 == 0) {
                                logger.info("Migrating {} to TMP_{} (no check) {}/{}", table, table, index, size);
                                ps.executeBatch();
                                tosend = 0;
                            }
                        }
                        // Final statement
                        if (tosend > 0) {
                            logger.info("Migrating {} to TMP_{} (no check) {}/{}", table, table, index, size);
                            ps.executeBatch();
                        }
                        return null;
                    }
            );
            return size;
        });

        tx(() -> {
            // Copying the tmp
            logger.info("Copying TMP_{} into {}...", table, table);
            postgresql.getJdbcOperations().execute(format("INSERT INTO %s SELECT * FROM TMP_%s", table, table));

            // Deletes tmp table
            logger.info("Deleting TMP_{}...", table);
            postgresql.getJdbcOperations().execute(format("DROP TABLE TMP_%s;", table));
        });

        // OK
        logger.info("{} count = {}...", table, count);
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
                "BUILD_LINKS",
                "VALIDATION_RUN_STATUSES",
                "VALIDATION_RUNS",
                "VALIDATION_STAMPS",
        };
        tx(() -> Arrays.stream(tables).forEach(table -> {
            Integer max = postgresql.queryForObject(
                    format("SELECT MAX(ID) AS ID FROM %s", table),
                    Collections.emptyMap(),
                    Integer.class
            );
            int value = max != null ? max + 1 : 1;
            postgresql.update(
                    format("ALTER SEQUENCE %s_ID_SEQ RESTART WITH %d", table, value),
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
                "EVENTS",
                "SETTINGS",
                "STORAGE",
        };
        tx(() -> {
            for (String table : tables) {
                postgresql.update(format("DELETE FROM %s", table), Collections.emptyMap());
            }
        });
    }

    private void tx(Runnable task) {
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    task.run();
                } catch (Exception ex) {
                    String message = ex.getMessage();
                    if (StringUtils.contains(message, "Block not found") && migrationProperties.isSkipBlobErrors()) {
                        logger.warn("Ignoring BLOB read error: {}", message);
                    } else {
                        throw ex;
                    }
                }
            }
        });
    }

    private <T> T intx(Supplier<T> supplier) {
        return txTemplate.execute(status -> supplier.get());
    }

    private void copy(String table, String... columns) {
        String h2Query = format("SELECT * FROM %s", table);

        String insert = Arrays.stream(columns).map(column -> StringUtils.substringBefore(column, "::")).collect(Collectors.joining(","));
        String values = Arrays.stream(columns).map(column -> ":" + column).collect(Collectors.joining(","));
        String postgresqlUpdate = format("INSERT INTO %s (%s) VALUES (%s)", table, insert, values);

        tx(() -> simpleMigration(
                table,
                h2Query,
                Collections.emptyMap(),
                postgresqlUpdate
        ));
    }

    private void simpleMigration(String name, String h2Query, Map<String, Object> h2Params, String postgresqlUpdate) {
        logger.info("Migrating {}...", name);
        List<Map<String, Object>> sources = h2.queryForList(h2Query, h2Params);
        int count = sources.size();
        @SuppressWarnings("unchecked")
        Map<String, ?>[] array = sources.toArray(new Map[sources.size()]);
        postgresql.batchUpdate(postgresqlUpdate, array);
        logger.info("{} count = {}...", name, count);
    }

}

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

    private final NamedParameterJdbcTemplate h2;
    private final NamedParameterJdbcTemplate postgresql;
    private final TransactionTemplate txTemplate;

    @Autowired
    public Migration(@Qualifier("h2") DataSource h2Datasource, @Qualifier("postgresql") DataSource postgresqlDatasource) {
        h2 = new NamedParameterJdbcTemplate(h2Datasource);
        postgresql = new NamedParameterJdbcTemplate(postgresqlDatasource);

        PlatformTransactionManager txManager = new DataSourceTransactionManager(postgresqlDatasource);
        txTemplate = new TransactionTemplate(txManager);
    }

    public void run() {
        // Projects
        migrateProjects();
        // FIXME Method net.nemerosa.ontrack.migration.postgresql.Migration.run
    }

    private void migrateProjects() {
        logger.info("Migrating projects...");
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                simpleMigration(
                        "Projects",
                        "SELECT * FROM PROJECTS",
                        Collections.emptyMap(),
                        "INSERT INTO PROJECTS (ID, NAME, DESCRIPTION, DISABLED) VALUES (:ID, :NAME, :DESCRIPTION, :DISABLED)"
                );
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

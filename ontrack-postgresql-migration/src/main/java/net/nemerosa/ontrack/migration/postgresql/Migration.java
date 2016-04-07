package net.nemerosa.ontrack.migration.postgresql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class Migration {

    private final NamedParameterJdbcTemplate h2;
    private final NamedParameterJdbcTemplate postgresql;

    @Autowired
    public Migration(@Qualifier("h2") DataSource h2Datasource, @Qualifier("postgresql") DataSource postgresqlDatasource) {
        h2 = new NamedParameterJdbcTemplate(h2Datasource);
        postgresql = new NamedParameterJdbcTemplate(postgresqlDatasource);
    }

    public void run() {
        // FIXME Method net.nemerosa.ontrack.migration.postgresql.Migration.run

    }

}

package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.support.ApplicationLogEntry;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class ApplicationLogEntriesJdbcRepository extends AbstractJdbcRepository
        implements ApplicationLogEntriesRepository {

    @Autowired
    public ApplicationLogEntriesJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void log(ApplicationLogEntry entry) {
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO APPLICATION_LOG_ENTRIES(LEVEL, TIMESTAMP, AUTHENTICATION," +
                        "NAME, DESCRIPTION, INFORMATION, EXCEPTION, DETAILS) VALUES (" +
                        ":level, :timestamp, :authentication, " +
                        ":name, :description, :information, :exception, :details" +
                        ")",
                params("level", entry.getLevel().name())
                        .addValue("timestamp", dateTimeForDB(entry.getTimestamp()))
                        .addValue("authentication", entry.getAuthentication())
                        .addValue("name", entry.getType().getName())
                        .addValue("description", entry.getType().getDescription())
                        .addValue("information", entry.getInformation())
                        .addValue("exception", entry.getStacktrace())
                        .addValue("details", getDetailsAsJson(entry.getDetails()))
        );
    }

    private String getDetailsAsJson(Map<String, String> details) {
        return details != null ? writeJson(details) : "{}";
    }

}

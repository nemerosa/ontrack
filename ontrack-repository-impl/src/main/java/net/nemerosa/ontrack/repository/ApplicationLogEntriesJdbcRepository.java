package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.exceptions.JsonParsingException;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.support.ApplicationLogEntry;
import net.nemerosa.ontrack.model.support.ApplicationLogEntryFilter;
import net.nemerosa.ontrack.model.support.ApplicationLogEntryLevel;
import net.nemerosa.ontrack.model.support.Page;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
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

    @Override
    public int getTotalCount() {
        return getFirstItem(
                "SELECT COUNT(*) FROM APPLICATION_LOG_ENTRIES",
                noParams(),
                Integer.class
        );
    }

    @Override
    public List<ApplicationLogEntry> getLogEntries(ApplicationLogEntryFilter filter, Page page) {
        int total = getTotalCount();
        int offset = page.getOffset();
        int count = page.getCount();
        if (offset >= total) {
            return Collections.emptyList();
        } else {
            // Query
            MapSqlParameterSource params = noParams();
            StringBuilder query = new StringBuilder(
                    "SELECT * FROM APPLICATION_LOG_ENTRIES WHERE 1 = 1 "
            );
            // Criteria: level
            if (filter.getLevel() != null) {
                query.append(" AND LEVEL = :level");
                params = params.addValue("level", filter.getLevel().name());
            }
            // Criteria: before
            if (filter.getBefore() != null) {
                query.append(" AND TIMESTAMP <= :before");
                params = params.addValue("before", dateTimeForDB(filter.getBefore()));
            }
            // Criteria: after
            if (filter.getAfter() != null) {
                query.append(" AND TIMESTAMP >= :after");
                params = params.addValue("after", dateTimeForDB(filter.getAfter()));
            }
            // Criteria: authentication
            if (filter.getAuthentication() != null) {
                query.append(" AND AUTHENTICATION = :authentication");
                params = params.addValue("authentication", filter.getAuthentication());
            }
            // Criteria: text
            if (StringUtils.isNotBlank(filter.getText())) {
                query.append(" AND (NAME LIKE :text OR DESCRIPTION LIKE :text OR INFORMATION LIKE :text OR DETAILS LIKE :text)");
                params = params.addValue("text", "%" + filter.getText() + "%");
            }
            // Ordering
            query.append(" ORDER BY ID DESC");
            // Performing the query
            // FIXME Pagination
            return getNamedParameterJdbcTemplate().query(
                    query.toString(),
                    params,
                    (rs, rowNum) -> new ApplicationLogEntry(
                            getEnum(ApplicationLogEntryLevel.class, rs, "LEVEL"),
                            dateTimeFromDB(rs.getString("TIMESTAMP")),
                            rs.getString("AUTHENTICATION"),
                            NameDescription.nd(
                                    rs.getString("NAME"),
                                    rs.getString("DESCRIPTION")
                            ),
                            rs.getString("INFORMATION"),
                            rs.getString("EXCEPTION"),
                            getDetailsFromJson(rs, "DETAILS")
                    )
            );
        }
    }

    private Map<String, String> getDetailsFromJson(ResultSet rs, String column) throws SQLException {
        try {
            //noinspection unchecked
            return (Map<String, String>) JsonUtils.toMap(
                    readJson(rs, column)
            );
        } catch (IOException e) {
            throw new JsonParsingException(e);
        }
    }

    private String getDetailsAsJson(Map<String, String> details) {
        return details != null ? writeJson(details) : "{}";
    }

}

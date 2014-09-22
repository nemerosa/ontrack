package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class EventJdbcRepository extends AbstractJdbcRepository implements EventRepository {

    @Autowired
    public EventJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void post(Event event) {
        StringBuilder sql = new StringBuilder("INSERT INTO EVENTS(EVENT_VALUES, EVENT_TIME, EVENT_USER, EVENT_TEMPLATE");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("eventValues", writeJson(event.getValues()));
        params.addValue("eventTime", dateTimeForDB(event.getSignature().getTime()));
        params.addValue("eventUser", event.getSignature().getUser().getName());
        params.addValue("eventTemplate", event.getTemplate());

        for (ProjectEntityType type : event.getEntities().keySet()) {
            sql.append(", ").append(type.name());
        }
        sql.append(") VALUES (:eventValues, :eventTime, :eventUser, :eventTemplate");
        for (Map.Entry<ProjectEntityType, ProjectEntity> entry : event.getEntities().entrySet()) {
            ProjectEntityType type = entry.getKey();
            ProjectEntity entity = entry.getValue();
            String typeEntry = type.name().toLowerCase();
            sql.append(", :").append(typeEntry);
            params.addValue(typeEntry, entity.id());
        }
        sql.append(")");

        getNamedParameterJdbcTemplate().update(
                sql.toString(),
                params
        );
    }

}

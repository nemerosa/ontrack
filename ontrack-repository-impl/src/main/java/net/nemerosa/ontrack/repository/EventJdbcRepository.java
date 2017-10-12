package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventType;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.model.support.NameValue;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.String.format;

@Repository
public class EventJdbcRepository extends AbstractJdbcRepository implements EventRepository {

    @Autowired
    public EventJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void post(Event event) {
        StringBuilder sql = new StringBuilder("INSERT INTO EVENTS(EVENT_VALUES, EVENT_TIME, EVENT_USER, EVENT_TYPE, REF");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("eventValues", writeJson(event.getValues()));
        params.addValue("eventTime", dateTimeForDB(event.getSignature().getTime()));
        params.addValue("eventUser", event.getSignature().getUser().getName());
        params.addValue("eventType", event.getEventType().getId());
        params.addValue("ref", event.getRef() != null ? event.getRef().name() : null);

        for (ProjectEntityType type : event.getEntities().keySet()) {
            sql.append(", ").append(type.name());
        }
        sql.append(") VALUES (:eventValues, :eventTime, :eventUser, :eventType, :ref");
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

    @Override
    public List<Event> query(List<Integer> allowedProjects,
                             int offset,
                             int count,
                             BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
                             Function<String, EventType> eventTypeLoader) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM EVENTS WHERE PROJECT IS NULL OR PROJECT IN (:projects)" +
                        " ORDER BY ID DESC" +
                        " LIMIT :count OFFSET :offset",
                params("projects", allowedProjects)
                        .addValue("count", count)
                        .addValue("offset", offset),
                (rs, num) -> toEvent(rs, entityLoader, eventTypeLoader)
        );
    }

    @Override
    public List<Event> query(List<Integer> allowedProjects,
                             ProjectEntityType entityType,
                             ID entityId,
                             int offset,
                             int count,
                             BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
                             Function<String, EventType> eventTypeLoader) {
        return getNamedParameterJdbcTemplate().query(
                format("SELECT * FROM EVENTS WHERE %s = :entityId", entityType.name()) +
                        " AND PROJECT IN (:projects)" +
                        " ORDER BY ID DESC" +
                        " LIMIT :count OFFSET :offset",
                params("entityId", entityId.get())
                        .addValue("projects", allowedProjects)
                        .addValue("count", count)
                        .addValue("offset", offset),
                (rs, num) -> toEvent(rs, entityLoader, eventTypeLoader)
        );
    }

    @Override
    public List<Event> query(List<Integer> allowedProjects,
                             EventType eventType,
                             ProjectEntityType entityType,
                             ID entityId,
                             int offset,
                             int count,
                             BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
                             Function<String, EventType> eventTypeLoader) {
        return getNamedParameterJdbcTemplate().query(
                format("SELECT * FROM EVENTS WHERE %s = :entityId", entityType.name()) +
                        " AND EVENT_TYPE = :eventType" +
                        " AND PROJECT IN (:projects)" +
                        " ORDER BY ID DESC" +
                        " LIMIT :count OFFSET :offset",
                params("entityId", entityId.get())
                        .addValue("eventType", eventType.getId())
                        .addValue("projects", allowedProjects)
                        .addValue("count", count)
                        .addValue("offset", offset),
                (rs, num) -> toEvent(rs, entityLoader, eventTypeLoader)
        );
    }

    @Override
    public Optional<Signature> getLastEventSignature(ProjectEntityType entityType, ID entityId, EventType eventType) {
        return getOptional(
                format("SELECT * FROM EVENTS WHERE %s = :entityId AND EVENT_TYPE = :eventType ORDER BY ID DESC LIMIT 1", entityType.name()),
                params("entityId", entityId.get()).addValue("eventType", eventType.getId()),
                (ResultSet rs, int num) -> readSignature(rs, "event_time", "event_user")
        );
    }

    @Override
    public Optional<Event> getLastEvent(ProjectEntityType entityType, ID entityId, EventType eventType,
                                        BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
                                        Function<String, EventType> eventTypeLoader) {
        return getOptional(
                format("SELECT * FROM EVENTS WHERE %s = :entityId AND EVENT_TYPE = :eventType ORDER BY ID DESC LIMIT 1", entityType.name()),
                params("entityId", entityId.get()).addValue("eventType", eventType.getId()),
                (ResultSet rs, int num) -> toEvent(rs, entityLoader, eventTypeLoader)
        );
    }

    private Event toEvent(ResultSet rs,
                          BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
                          Function<String, EventType> eventTypeLoader) throws SQLException {
        // Event type name
        String eventTypeName = rs.getString("event_type");
        // Signature
        Signature signature = readSignature(rs, "event_time", "event_user");
        // Entities
        Map<ProjectEntityType, ProjectEntity> entities = new LinkedHashMap<>();
        for (ProjectEntityType type : ProjectEntityType.values()) {
            int entityId = rs.getInt(type.name());
            if (!rs.wasNull()) {
                ProjectEntity entity = entityLoader.apply(type, ID.of(entityId));
                entities.put(type, entity);
            }
        }
        // Reference (if any)
        ProjectEntityType refEntity = getEnum(ProjectEntityType.class, rs, "ref");
        // Values
        Map<String, NameValue> values = loadValues(rs);
        // OK
        return new Event(
                eventTypeLoader.apply(eventTypeName),
                signature,
                entities,
                refEntity,
                values
        );
    }

    private Map<String, NameValue> loadValues(ResultSet rs) throws SQLException {
        Map<String, NameValue> map = new LinkedHashMap<>();
        JsonNode node = readJson(rs, "event_values");
        Iterator<Map.Entry<String, JsonNode>> i = node.fields();
        while (i.hasNext()) {
            Map.Entry<String, JsonNode> child = i.next();
            String key = child.getKey();
            JsonNode nameValue = child.getValue();
            map.put(
                    key,
                    new NameValue(
                            nameValue.path("name").asText(),
                            nameValue.path("value").asText()
                    )
            );
        }
        return map;
    }

}

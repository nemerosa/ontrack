package db.migration;


import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventType;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class V11__476_ProjectEntityCreationMigration extends BaseJavaMigration {

    private final Logger logger = LoggerFactory.getLogger(V11__476_ProjectEntityCreationMigration.class);

    @Override
    public void migrate(Context context) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(context.getConfiguration().getDataSource());
        logger.info("Migrating the project entity creation events...");
        migrateCreation(jdbcTemplate, EventFactory.NEW_PROJECT, "PROJECT", "PROJECTS");
        migrateCreation(jdbcTemplate, EventFactory.NEW_BRANCH, "BRANCH", "BRANCHES");
        migrateCreation(jdbcTemplate, EventFactory.NEW_PROMOTION_LEVEL, "PROMOTION_LEVEL", "PROMOTION_LEVELS");
        migrateCreation(jdbcTemplate, EventFactory.NEW_VALIDATION_STAMP, "VALIDATION_STAMP", "VALIDATION_STAMPS");
    }

    private void migrateCreation(
            JdbcTemplate jdbcTemplate,
            EventType eventType,
            String eventLinkColumn,
            String entityTable) {
        jdbcTemplate.execute(
                connection -> connection.prepareStatement(
                        String.format("SELECT * FROM %s", entityTable),
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE),
                (PreparedStatementCallback<Void>) psEntity -> {
                    try (ResultSet rsEntity = psEntity.executeQuery()) {
                        while (rsEntity.next()) {
                            // Gets the entity ID
                            int entityId = rsEntity.getInt("ID");
                            // Statement for the events
                            jdbcTemplate.execute(
                                    connection -> connection.prepareStatement(
                                            String.format("SELECT EVENT_TIME, EVENT_USER FROM EVENTS WHERE %s = ? AND EVENT_TYPE = ?", eventLinkColumn)
                                    ),
                                    (PreparedStatementCallback<Void>) psEvent -> {
                                        psEvent.setInt(1, entityId);
                                        psEvent.setString(2, eventType.getId());
                                        try (ResultSet rsEvent = psEvent.executeQuery()) {
                                            if (rsEvent.next()) {
                                                String creation = rsEvent.getString(1);
                                                String creator = rsEvent.getString(2);
                                                // Update of the project entity
                                                rsEntity.updateString("CREATION", creation);
                                                rsEntity.updateString("CREATOR", creator);
                                                rsEntity.updateRow();
                                            }
                                        }
                                        return null;
                                    }
                            );
                        }
                        return null;
                    }
                }
        );
    }
}
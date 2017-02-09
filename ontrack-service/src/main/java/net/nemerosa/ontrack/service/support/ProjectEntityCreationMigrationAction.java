package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventType;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProjectEntityCreationMigrationAction implements DBMigrationAction {

    @Override
    public int getPatch() {
        return 37;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        migrateCreation(connection, EventFactory.NEW_PROJECT, "PROJECT", "PROJECTS");
        migrateCreation(connection, EventFactory.NEW_BRANCH, "BRANCH", "BRANCHES");
        migrateCreation(connection, EventFactory.NEW_PROMOTION_LEVEL, "PROMOTION_LEVEL", "PROMOTION_LEVELS");
        migrateCreation(connection, EventFactory.NEW_VALIDATION_STAMP, "VALIDATION_STAMP", "VALIDATION_STAMPS");
    }

    private void migrateCreation(
            Connection connection,
            EventType eventType,
            String eventLinkColumn,
            String entityTable) throws SQLException {
        // Gets all project entities
        try (PreparedStatement psEntity = connection.prepareStatement(
                String.format("SELECT * FROM %s", entityTable),
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE)) {
            try (ResultSet rsEntity = psEntity.executeQuery()) {
                while (rsEntity.next()) {
                    // Gets the entity ID
                    int entityId = rsEntity.getInt("ID");
                    // Statement for the events
                    try (PreparedStatement psEvent = connection.prepareStatement(
                            String.format("SELECT EVENT_TIME, EVENT_USER FROM EVENTS WHERE %s = ? AND EVENT_TYPE = ?", eventLinkColumn)
                    )) {
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
                    }
                }
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Computing creation signature for project entities";
    }
}

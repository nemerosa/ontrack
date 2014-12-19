package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;

@Component
public class TemplateSyncEventMigrationAction implements DBMigrationAction {

    @Override
    public int getPatch() {
        return 9;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM EVENTS WHERE EVENT_TYPE LIKE 'sync_%';")) {
            ps.executeUpdate();
        }
    }

    @Override
    public String getDisplayName() {
        return "Removal of template sync. events";
    }
}

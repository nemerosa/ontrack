package net.nemerosa.ontrack.model.support;

import java.sql.Connection;

public interface DBMigrationAction {

    int getPatch();

    void migrate(Connection connection);

    String getDisplayName();
}

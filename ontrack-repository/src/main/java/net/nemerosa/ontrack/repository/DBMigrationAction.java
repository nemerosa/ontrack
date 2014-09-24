package net.nemerosa.ontrack.repository;

import java.sql.Connection;

public interface DBMigrationAction {

    int getPatch();

    void migrate(Connection connection);

}

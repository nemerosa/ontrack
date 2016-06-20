package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * Migration action used to test the migration of build links
 *
 * @see net.nemerosa.ontrack.service.support.BuildLinkMigrationAction
 */
@Component
public class TestBuildLinkMigrationPreparation implements DBMigrationAction {

    /**
     * Makes sure it runs BEFORE the migration
     */
    @Override
    public int getPatch() {
        return 30;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        // Creates source build

        // FIXME Method net.nemerosa.ontrack.service.TestBuildLinkMigrationPreparation.migrate

    }

    @Override
    public String getDisplayName() {
        return "Preparation for the test of the migration of build links";
    }
}

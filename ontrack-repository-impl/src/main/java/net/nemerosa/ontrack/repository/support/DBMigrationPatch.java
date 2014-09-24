package net.nemerosa.ontrack.repository.support;

import net.nemerosa.ontrack.model.support.DBMigrationAction;
import net.sf.dbinit.DBPatchAction;
import org.apache.commons.lang3.Validate;

import java.sql.Connection;

public class DBMigrationPatch implements DBPatchAction {

    private final int patch;
    private final DBMigrationAction migrationAction;

    public DBMigrationPatch(int patch, DBMigrationAction migrationAction) {
        this.patch = patch;
        this.migrationAction = migrationAction;
    }

    @Override
    public String getDisplayName() {
        return migrationAction.getDisplayName();
    }

    @Override
    public boolean appliesTo(int patch) {
        return this.patch == patch;
    }

    @Override
    public void apply(Connection connection, int patch) throws Exception {
        Validate.isTrue(this.patch == patch);
        migrationAction.migrate(connection);
    }
}

package net.nemerosa.ontrack.repository.support;

import net.sf.dbinit.DBInit;

import java.sql.Connection;
import java.sql.SQLException;

public class ConfiguredDBInit extends DBInit {

    @Override
    protected Connection getConnection() throws SQLException {
        Connection c = super.getConnection();
        c.setAutoCommit(false);
        return c;
    }
}

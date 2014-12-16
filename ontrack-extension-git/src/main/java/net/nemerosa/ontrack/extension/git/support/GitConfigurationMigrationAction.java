package net.nemerosa.ontrack.extension.git.support;

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Migration task for #172, for changing the type of the configurations.
 */
@Component
public class GitConfigurationMigrationAction implements DBMigrationAction {

    @Override
    public int getPatch() {
        return 8;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        // For all Git configurations
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM CONFIGURATIONS WHERE TYPE = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setString(1, "net.nemerosa.ontrack.extension.git.model.GitConfiguration");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rs.updateString("TYPE", BasicGitConfiguration.class.getName());
                    rs.updateRow();
                }
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Git configuration type name";
    }
}

package net.nemerosa.ontrack.extension.github.support;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import org.junit.Test;

public class GitHubEngineConfigurationMigrationActionIT extends AbstractServiceTestSupport {

    /**
     * Test of the migration.
     * <p>
     * An old GitHub configuration is created with name "OntrackTest"
     * (see {@link TestGitHubEngineConfigurationMigrationPreparationAction})
     * <p>
     * A project P is created, using this configuration.
     * <p>
     * The test must check that the old configuration has been migrated and that the project
     * is now using a configuration including the repository, the indexation interval.
     */
    @Test
    public void migration() throws Exception {
    }

}

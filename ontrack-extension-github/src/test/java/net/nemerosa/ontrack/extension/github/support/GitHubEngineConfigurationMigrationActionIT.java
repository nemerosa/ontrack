package net.nemerosa.ontrack.extension.github.support;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException;
import net.nemerosa.ontrack.model.security.ProjectList;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GitHubEngineConfigurationMigrationActionIT extends AbstractServiceTestSupport {

    @Autowired
    private StructureService structureService;

    @Autowired
    private PropertyService propertyService;

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
        // Gets the project
        Project project = asUser().with(ProjectList.class).call(() ->
                        structureService.findProjectByName("GitHubEngineConfigurationMigrationAction")
        ).orElseThrow(() -> new ProjectNotFoundException("GitHubEngineConfigurationMigrationAction"));
    }

}

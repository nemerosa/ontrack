package net.nemerosa.ontrack.extension.github.support;

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.ProjectList;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class GitHubEngineConfigurationMigrationActionIT extends AbstractServiceTestSupport {

    @Autowired
    private StructureService structureService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private EncryptionService encryptionService;

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

        // Gets the migrated configuration
        GitHubEngineConfiguration gitHubEngineConfiguration = configurationRepository.find(GitHubEngineConfiguration.class, "OntrackTest")
                .orElseThrow(() -> new RuntimeException("Cannot find \"OntrackTest\" configuration"));
        assertEquals("OntrackTest", gitHubEngineConfiguration.getName());
        assertEquals("https://github.com", gitHubEngineConfiguration.getUrl());
        assertEquals("user", gitHubEngineConfiguration.getUser());
        assertEquals("password", encryptionService.decrypt(gitHubEngineConfiguration.getPassword()));
        assertEquals("token", gitHubEngineConfiguration.getOauth2Token());

        // Gets the GitHub project property
        GitHubProjectConfigurationProperty configurationProperty = asUser().withView(project).call(() ->
                        propertyService.getProperty(project, GitHubProjectConfigurationPropertyType.class).option()
        ).orElseThrow(() -> new RuntimeException("Missing GitHub property on project"));
    }

}

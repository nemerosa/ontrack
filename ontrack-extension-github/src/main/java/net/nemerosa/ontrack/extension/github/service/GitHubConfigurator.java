package net.nemerosa.ontrack.extension.github.service;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitHubConfigurator implements GitConfigurator {

    private final PropertyService propertyService;

    @Autowired
    public GitHubConfigurator(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    public GitConfiguration configure(GitConfiguration configuration, Branch branch) {
        // Project GitHub configuration
        Property<GitHubProjectConfigurationProperty> projectConfig = propertyService.getProperty(branch.getProject(), GitHubProjectConfigurationPropertyType.class);
        if (!projectConfig.isEmpty()) {
            // GitHub configuration
            GitHubConfiguration gitHub = projectConfig.getValue().getConfiguration();
            // Merge the project configuration
            return configuration
                    .withRemote(gitHub.getRemote())
                    .withUser(gitHub.getUser())
                    .withPassword(gitHub.getPassword())
                    .withIndexationInterval(gitHub.getIndexationInterval())
                    .withCommitLink(gitHub.getCommitLink())
                    .withFileAtCommitLink(gitHub.getFileAtCommitLink())
                    // TODO .withIssueServiceConfigurationIdentifier("")
                    ;
        } else {
            return configuration;
        }
    }
}

package net.nemerosa.ontrack.extension.gitlab.property;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.gitlab.GitLabIssueServiceExtension;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GitLabConfigurator implements GitConfigurator {

    private final PropertyService propertyService;
    private final IssueServiceRegistry issueServiceRegistry;
    private final GitLabIssueServiceExtension issueServiceExtension;

    @Autowired
    public GitLabConfigurator(PropertyService propertyService, IssueServiceRegistry issueServiceRegistry, GitLabIssueServiceExtension issueServiceExtension) {
        this.propertyService = propertyService;
        this.issueServiceRegistry = issueServiceRegistry;
        this.issueServiceExtension = issueServiceExtension;
    }

    @Override
    public Optional<GitConfiguration> getConfiguration(Project project) {
        return propertyService.getProperty(project, GitLabProjectConfigurationPropertyType.class)
                .option()
                .map(this::getGitConfiguration);
    }

    private GitConfiguration getGitConfiguration(GitLabProjectConfigurationProperty property) {
        return new GitLabGitConfiguration(
                property,
                getConfiguredIssueService(property)
        );
    }

    private ConfiguredIssueService getConfiguredIssueService(GitLabProjectConfigurationProperty property) {
        String identifier = property.getIssueServiceConfigurationIdentifier();
        if (IssueServiceConfigurationRepresentation.isSelf(identifier)) {
            return new ConfiguredIssueService(
                    issueServiceExtension,
                    new GitLabIssueServiceConfiguration(
                            property.getConfiguration(),
                            property.getRepository()
                    )
            );
        } else {
            return issueServiceRegistry.getConfiguredIssueService(identifier);
        }
    }
}

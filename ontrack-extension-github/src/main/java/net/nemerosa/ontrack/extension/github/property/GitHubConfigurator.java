package net.nemerosa.ontrack.extension.github.property;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.github.GitHubIssueServiceExtension;
import net.nemerosa.ontrack.extension.github.service.GitHubIssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationIdentifier;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static net.nemerosa.ontrack.extension.github.property.GitHubGitConfiguration.CONFIGURATION_REPOSITORY_SEPARATOR;

@Component
public class GitHubConfigurator implements GitConfigurator {

    private final PropertyService propertyService;
    private final IssueServiceRegistry issueServiceRegistry;
    private final GitHubIssueServiceExtension issueServiceExtension;

    @Autowired
    public GitHubConfigurator(
            PropertyService propertyService,
            IssueServiceRegistry issueServiceRegistry,
            GitHubIssueServiceExtension issueServiceExtension) {
        this.propertyService = propertyService;
        this.issueServiceRegistry = issueServiceRegistry;
        this.issueServiceExtension = issueServiceExtension;
    }

    @Override
    public Optional<GitConfiguration> getConfiguration(Project project) {
        return propertyService.getProperty(project, GitHubProjectConfigurationPropertyType.class)
                .option()
                .map(this::getGitConfiguration);
    }

    private GitConfiguration getGitConfiguration(GitHubProjectConfigurationProperty property) {
        return new GitHubGitConfiguration(
                property,
                getConfiguredIssueService(property)
        );
    }

    private ConfiguredIssueService getConfiguredIssueService(GitHubProjectConfigurationProperty property) {
        String identifier = property.getIssueServiceConfigurationIdentifier();
        if (StringUtils.isBlank(identifier) || IssueServiceConfigurationRepresentation.Companion.isSelf(identifier)) {
            return new ConfiguredIssueService(
                    issueServiceExtension,
                    new GitHubIssueServiceConfiguration(
                            property.getConfiguration(),
                            property.getRepository()
                    )
            );
        } else {
            return issueServiceRegistry.getConfiguredIssueService(
                    new IssueServiceConfigurationIdentifier(
                            GitHubIssueServiceExtension.GITHUB_SERVICE_ID,
                            property.getConfiguration().getName()
                                    + CONFIGURATION_REPOSITORY_SEPARATOR
                                    + property.getRepository()
                    ).format()
            );
        }
    }
}

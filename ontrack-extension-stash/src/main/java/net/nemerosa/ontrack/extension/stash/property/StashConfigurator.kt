package net.nemerosa.ontrack.extension.stash.property;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.git.model.GitPullRequest;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StashConfigurator implements GitConfigurator {

    private final PropertyService propertyService;
    private final IssueServiceRegistry issueServiceRegistry;

    @Autowired
    public StashConfigurator(PropertyService propertyService, IssueServiceRegistry issueServiceRegistry) {
        this.propertyService = propertyService;
        this.issueServiceRegistry = issueServiceRegistry;
    }

    @Override
    public boolean isProjectConfigured(@NotNull Project project) {
        return propertyService.hasProperty(project, StashProjectConfigurationPropertyType.class);
    }

    @Nullable
    @Override
    public GitConfiguration getConfiguration(@NotNull Project project) {
        return propertyService.getProperty(project, StashProjectConfigurationPropertyType.class)
                .option()
                .map(this::getGitConfiguration)
                .orElse(null);
    }

    @Nullable
    @Override
    public Integer toPullRequestID(@NotNull String key) {
        // TODO #690
        throw new UnsupportedOperationException("Pull requests not supported yet for Bitbucket");
    }

    @Nullable
    @Override
    public GitPullRequest getPullRequest(@NotNull GitConfiguration configuration, int id) {
        // TODO #690
        throw new UnsupportedOperationException("Pull requests not supported yet for Bitbucket");
    }

    private GitConfiguration getGitConfiguration(StashProjectConfigurationProperty property) {
        return new StashGitConfiguration(
                property,
                getConfiguredIssueService(property)
        );
    }

    private ConfiguredIssueService getConfiguredIssueService(StashProjectConfigurationProperty property) {
        return issueServiceRegistry.getConfiguredIssueService(
                property.getIssueServiceConfigurationIdentifier()
        );
    }
}

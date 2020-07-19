package net.nemerosa.ontrack.extension.git.support;

import net.nemerosa.ontrack.extension.git.model.BasicGitActualConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasicGitConfigurator implements GitConfigurator {

    private final PropertyService propertyService;
    private final IssueServiceRegistry issueServiceRegistry;

    @Autowired
    public BasicGitConfigurator(PropertyService propertyService, IssueServiceRegistry issueServiceRegistry) {
        this.propertyService = propertyService;
        this.issueServiceRegistry = issueServiceRegistry;
    }

    @Override
    public boolean isProjectConfigured(@NotNull Project project) {
        return propertyService.hasProperty(project, GitProjectConfigurationPropertyType.class);
    }

    @Nullable
    @Override
    public GitConfiguration getConfiguration(@NotNull Project project) {
        return propertyService.getProperty(project, GitProjectConfigurationPropertyType.class)
                .option()
                .map(this::getGitConfiguration)
                .orElse(null);
    }

    private GitConfiguration getGitConfiguration(GitProjectConfigurationProperty property) {
        return new BasicGitActualConfiguration(
                property.getConfiguration(),
                getConfiguredIssueService(property)
        );
    }

    private ConfiguredIssueService getConfiguredIssueService(GitProjectConfigurationProperty property) {
        String identifier = property.getConfiguration().getIssueServiceConfigurationIdentifier();
        return issueServiceRegistry.getConfiguredIssueService(identifier);
    }
}

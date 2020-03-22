package net.nemerosa.ontrack.extension.stash.property;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    @Override
    public Optional<GitConfiguration> getConfiguration(Project project) {
        return propertyService.getProperty(project, StashProjectConfigurationPropertyType.class)
                .option()
                .map(this::getGitConfiguration);
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

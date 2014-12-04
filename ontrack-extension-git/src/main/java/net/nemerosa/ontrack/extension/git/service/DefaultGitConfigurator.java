package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.FormerGitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultGitConfigurator implements GitConfigurator {

    private final PropertyService propertyService;
    private final BuildGitCommitLinkService buildGitCommitLinkService;

    @Autowired
    public DefaultGitConfigurator(PropertyService propertyService, BuildGitCommitLinkService buildGitCommitLinkService) {
        this.propertyService = propertyService;
        this.buildGitCommitLinkService = buildGitCommitLinkService;
    }

    @Override
    public FormerGitConfiguration configure(FormerGitConfiguration configuration, Branch branch) {
        FormerGitConfiguration thisConfig = configureProject(configuration, branch.getProject());
        Property<GitBranchConfigurationProperty> branchConfig = propertyService.getProperty(branch, GitBranchConfigurationPropertyType.class);
        if (!branchConfig.isEmpty()) {
            thisConfig = thisConfig.withBranch(branchConfig.getValue().getBranch());
            // Build commit link
            thisConfig = thisConfig.withBuildCommitLink(toConfiguredBuildGitCommitLink(branchConfig.getValue().getBuildCommitLink()));
        }
        // OK
        return thisConfig;
    }

    @Override
    public FormerGitConfiguration configureProject(FormerGitConfiguration configuration, Project project) {
        FormerGitConfiguration thisConfig = configuration;
        // Project Git configuration?
        Property<GitProjectConfigurationProperty> projectConfig = propertyService.getProperty(project, GitProjectConfigurationPropertyType.class);
        if (!projectConfig.isEmpty()) {
            // Merge the project configuration
            thisConfig = thisConfig.merge(projectConfig.getValue().getConfiguration());
        }
        // OK
        return thisConfig;
    }

    private <T> ConfiguredBuildGitCommitLink<T> toConfiguredBuildGitCommitLink(ServiceConfiguration serviceConfiguration) {
        @SuppressWarnings("unchecked")
        BuildGitCommitLink<T> link = (BuildGitCommitLink<T>) buildGitCommitLinkService.getLink(serviceConfiguration.getId());
        T linkData = link.parseData(serviceConfiguration.getData());
        return new ConfiguredBuildGitCommitLink<>(
                link,
                linkData
        );
    }
}

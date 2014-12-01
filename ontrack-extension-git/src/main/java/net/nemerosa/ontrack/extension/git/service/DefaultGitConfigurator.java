package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.git.GitRepository;
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
    public GitConfiguration configure(GitConfiguration configuration, Branch branch) {
        GitConfiguration thisConfig = configuration;
        // Project Git configuration?
        Property<GitProjectConfigurationProperty> projectConfig = propertyService.getProperty(branch.getProject(), GitProjectConfigurationPropertyType.class);
        if (!projectConfig.isEmpty()) {
            // Merge the project configuration
            thisConfig = thisConfig.merge(projectConfig.getValue().getConfiguration());
        }
        // ... and the branch's
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
    public GitConfiguration configureProject(GitConfiguration configuration, Project project) {
        GitConfiguration thisConfig = configuration;
        // Project Git configuration?
        Property<GitProjectConfigurationProperty> projectConfig = propertyService.getProperty(project, GitProjectConfigurationPropertyType.class);
        if (!projectConfig.isEmpty()) {
            // Merge the project configuration
            thisConfig = thisConfig.merge(projectConfig.getValue().getConfiguration());
        }
        // OK
        return thisConfig;
    }



    @Override
    public GitRepository configureRepository(final GitRepository repository, Project project) {
        GitRepository thisRepo = repository;
        // Project Git configuration?
        Property<GitProjectConfigurationProperty> projectConfig = propertyService.getProperty(project, GitProjectConfigurationPropertyType.class);
        if (!projectConfig.isEmpty()) {
            // Merge the project configuration
            thisRepo = thisRepo
                    .withRemote(projectConfig.getValue().getConfiguration().getRemote())
                    .withUser(projectConfig.getValue().getConfiguration().getUser())
                    .withUser(projectConfig.getValue().getConfiguration().getPassword());
        }
        // OK
        return thisRepo;
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

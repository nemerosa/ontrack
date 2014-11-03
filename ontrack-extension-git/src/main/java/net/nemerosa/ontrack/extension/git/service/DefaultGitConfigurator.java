package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultGitConfigurator implements GitConfigurator {

    private final PropertyService propertyService;

    @Autowired
    public DefaultGitConfigurator(PropertyService propertyService) {
        this.propertyService = propertyService;
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
            thisConfig = thisConfig.withTagPattern(branchConfig.getValue().getTagPattern());
        }
        // OK
        return thisConfig;
    }
}

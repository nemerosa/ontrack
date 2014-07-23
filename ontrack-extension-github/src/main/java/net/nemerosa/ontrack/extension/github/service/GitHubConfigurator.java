package net.nemerosa.ontrack.extension.github.service;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.model.structure.Branch;
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
        // FIXME Project GitHub configuration?
//        Property<GitHu> projectConfig = propertyService.getProperty(branch.getProject(), GitProjectConfigurationPropertyType.class);
//        if (!projectConfig.isEmpty()) {
//            // Merge the project configuration
//            GitConfiguration thisConfig = configuration.merge(projectConfig.getValue().getConfiguration());
//            // ... and the branch's
//            Property<GitBranchConfigurationProperty> branchConfig = propertyService.getProperty(branch, GitBranchConfigurationPropertyType.class);
//            if (!branchConfig.isEmpty()) {
//                thisConfig = thisConfig.withBranch(branchConfig.getValue().getBranch());
//            }
//            // OK
//            return thisConfig;
//        } else {
        return configuration;
//        }
    }
}

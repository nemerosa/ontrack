package net.nemerosa.ontrack.extension.gitlab.property;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GitLabConfigurator implements GitConfigurator {

    private final PropertyService propertyService;

    @Autowired
    public GitLabConfigurator(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    public Optional<GitConfiguration> getConfiguration(Project project) {
        return propertyService.getProperty(project, GitLabProjectConfigurationPropertyType.class)
                .option()
                .map(GitLabProjectConfigurationProperty::getGitConfiguration);
    }
}

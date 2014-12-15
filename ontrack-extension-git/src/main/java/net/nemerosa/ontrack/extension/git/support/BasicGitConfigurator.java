package net.nemerosa.ontrack.extension.git.support;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BasicGitConfigurator implements GitConfigurator {

    private final PropertyService propertyService;

    @Autowired
    public BasicGitConfigurator(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    public Optional<GitConfiguration> getConfiguration(Project project) {
        return propertyService.getProperty(project, GitProjectConfigurationPropertyType.class)
                .option()
                .map(GitProjectConfigurationProperty::getConfiguration);

    }
}

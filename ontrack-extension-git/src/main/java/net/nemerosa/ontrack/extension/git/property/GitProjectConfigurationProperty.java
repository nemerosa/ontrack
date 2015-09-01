package net.nemerosa.ontrack.extension.git.property;

import lombok.Data;
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration;
import net.nemerosa.ontrack.model.support.ConfigurationProperty;

@Data
public class GitProjectConfigurationProperty implements ConfigurationProperty<BasicGitConfiguration> {

    /**
     * Link to the Git configuration
     */
    private final BasicGitConfiguration configuration;

}

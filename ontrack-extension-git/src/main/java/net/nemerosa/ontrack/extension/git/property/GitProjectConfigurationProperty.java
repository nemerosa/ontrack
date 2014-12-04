package net.nemerosa.ontrack.extension.git.property;

import lombok.Data;
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration;

@Data
public class GitProjectConfigurationProperty {

    /**
     * Link to the Git configuration
     */
    private final BasicGitConfiguration configuration;

}

package net.nemerosa.ontrack.extension.git.property;

import lombok.Data;
import net.nemerosa.ontrack.extension.git.model.FormerGitConfiguration;

@Data
public class GitProjectConfigurationProperty {

    /**
     * Link to the Git configuration
     */
    private final FormerGitConfiguration configuration;

}

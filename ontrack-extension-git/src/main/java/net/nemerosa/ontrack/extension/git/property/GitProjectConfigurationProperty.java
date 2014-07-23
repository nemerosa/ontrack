package net.nemerosa.ontrack.extension.git.property;

import lombok.Data;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;

@Data
public class GitProjectConfigurationProperty {

    /**
     * Link to the Git configuration
     */
    private final GitConfiguration configuration;

}

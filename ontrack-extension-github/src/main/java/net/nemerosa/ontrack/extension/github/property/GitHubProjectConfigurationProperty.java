package net.nemerosa.ontrack.extension.github.property;

import lombok.Data;
import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;

@Data
public class GitHubProjectConfigurationProperty {

    /**
     * Link to the GitHub configuration
     */
    private final GitHubConfiguration configuration;

}

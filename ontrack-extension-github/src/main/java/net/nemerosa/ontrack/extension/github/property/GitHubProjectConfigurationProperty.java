package net.nemerosa.ontrack.extension.github.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.model.support.ConfigurationProperty;

@Data
public class GitHubProjectConfigurationProperty implements ConfigurationProperty<GitHubEngineConfiguration> {

    /**
     * Link to the GitHub configuration
     */
    private final GitHubEngineConfiguration configuration;

    /**
     * Repository name
     */
    private final String repository;

    /**
     * Indexation interval
     */
    private final int indexationInterval;

    /**
     * Creates a Git configuration from the project's configuration.
     */
    @JsonIgnore
    public GitConfiguration getGitConfiguration() {
        return new GitHubGitConfiguration(this);
    }

}

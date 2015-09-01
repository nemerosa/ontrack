package net.nemerosa.ontrack.extension.stash.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
import net.nemerosa.ontrack.model.support.ConfigurationProperty;

@Data
public class StashProjectConfigurationProperty implements ConfigurationProperty<StashConfiguration> {

    /**
     * Link to the Stash configuration
     */
    private final StashConfiguration configuration;

    /**
     * Project in Stash
     */
    private final String project;

    /**
     * Repository in the project
     */
    private final String repository;

    /**
     * Link to the repository
     */
    public String getRepositoryUrl() {
        return String.format(
                "%s/projects/%s/repos/%s",
                configuration.getUrl(),
                project,
                repository
        );
    }

    /**
     * Creates a Git configuration from the project's configuration.
     */
    @JsonIgnore
    public GitConfiguration getGitConfiguration() {
        return new StashGitConfiguration(this);
    }
}

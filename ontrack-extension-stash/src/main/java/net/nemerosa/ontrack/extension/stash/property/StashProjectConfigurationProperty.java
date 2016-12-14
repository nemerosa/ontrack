package net.nemerosa.ontrack.extension.stash.property;

import lombok.Data;
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
    @SuppressWarnings("unused")
    public String getRepositoryUrl() {
        return String.format(
                "%s/projects/%s/repos/%s",
                configuration.getUrl(),
                project,
                repository
        );
    }

}

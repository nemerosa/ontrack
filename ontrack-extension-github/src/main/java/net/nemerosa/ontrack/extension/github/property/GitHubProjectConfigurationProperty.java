package net.nemerosa.ontrack.extension.github.property;

import lombok.Data;
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
     * ID to the {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration} associated
     * with this repository.
     */
    private final String issueServiceConfigurationIdentifier;

}

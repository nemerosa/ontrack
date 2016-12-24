package net.nemerosa.ontrack.extension.gitlab.property;

import lombok.Data;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
import net.nemerosa.ontrack.model.support.ConfigurationProperty;

@Data
public class GitLabProjectConfigurationProperty implements ConfigurationProperty<GitLabConfiguration> {

    /**
     * Link to the GitLab configuration
     */
    private final GitLabConfiguration configuration;

    /**
     * ID to the {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration} associated
     * with this repository.
     */
    private final String issueServiceConfigurationIdentifier;

    /**
     * Repository name
     */
    private final String repository;

    /**
     * Indexation interval
     */
    private final int indexationInterval;

}

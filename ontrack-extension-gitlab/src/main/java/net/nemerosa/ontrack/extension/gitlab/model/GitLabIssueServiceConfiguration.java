package net.nemerosa.ontrack.extension.gitlab.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.gitlab.GitLabIssueServiceExtension;
import net.nemerosa.ontrack.extension.gitlab.property.GitLabGitConfiguration;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;

@Data
public class GitLabIssueServiceConfiguration implements IssueServiceConfiguration {

    private final GitLabConfiguration configuration;
    private final String repository;

    @Override
    public String getServiceId() {
        return GitLabIssueServiceExtension.GITLAB_SERVICE_ID;
    }

    @Override
    public String getName() {
        return configuration.getName() +
                GitLabGitConfiguration.CONFIGURATION_REPOSITORY_SEPARATOR +
                repository;
    }
}

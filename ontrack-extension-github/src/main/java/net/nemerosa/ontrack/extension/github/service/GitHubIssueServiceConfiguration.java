package net.nemerosa.ontrack.extension.github.service;

import net.nemerosa.ontrack.extension.github.GitHubIssueServiceExtension;
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;

public class GitHubIssueServiceConfiguration implements IssueServiceConfiguration {

    private final GitHubEngineConfiguration configuration;

    public GitHubIssueServiceConfiguration(GitHubEngineConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getServiceId() {
        return GitHubIssueServiceExtension.GITHUB_SERVICE_ID;
    }

    @Override
    public String getName() {
        return configuration.getName();
    }
}

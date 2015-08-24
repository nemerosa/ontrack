package net.nemerosa.ontrack.extension.github.service;

import net.nemerosa.ontrack.extension.github.GitHubIssueServiceExtension;
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;

import static java.lang.String.format;

public class GitHubIssueServiceConfiguration implements IssueServiceConfiguration {

    private final GitHubEngineConfiguration configuration;
    private final String repository;

    public GitHubIssueServiceConfiguration(GitHubEngineConfiguration configuration, String repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    @Override
    public String getServiceId() {
        return GitHubIssueServiceExtension.GITHUB_SERVICE_ID;
    }

    @Override
    public String getName() {
        return format(
                "%s:%s",
                configuration.getName(),
                repository
        );
    }
}

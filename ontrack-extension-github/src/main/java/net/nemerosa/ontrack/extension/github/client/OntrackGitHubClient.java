package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.extension.github.model.GitHubIssue;

public interface OntrackGitHubClient {

    GitHubIssue getIssue(String project, GitHubClientConfigurator configurator, int id);

}

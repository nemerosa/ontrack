package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.extension.github.model.GitHubIssue;

public interface OntrackGitHubClient {

    GitHubIssue getIssue(int id);

}

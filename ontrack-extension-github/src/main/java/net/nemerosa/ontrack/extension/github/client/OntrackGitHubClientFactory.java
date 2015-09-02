package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;

/**
 * Creates a client for accessing GitHub.
 */
public interface OntrackGitHubClientFactory {

    /**
     * Given a GitHub engine configuration, creates a GitHub client.
     */
    OntrackGitHubClient create(GitHubEngineConfiguration configuration);

}

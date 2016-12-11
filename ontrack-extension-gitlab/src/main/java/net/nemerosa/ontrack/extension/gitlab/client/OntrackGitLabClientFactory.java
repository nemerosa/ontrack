package net.nemerosa.ontrack.extension.gitlab.client;

import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;

/**
 * Creates a client for accessing GitLab.
 */
public interface OntrackGitLabClientFactory {

    /**
     * Given a GitLab engine configuration, creates a GitLab client.
     */
    OntrackGitLabClient create(GitLabConfiguration configuration);

}

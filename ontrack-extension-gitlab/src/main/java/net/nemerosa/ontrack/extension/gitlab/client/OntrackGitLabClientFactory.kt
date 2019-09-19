package net.nemerosa.ontrack.extension.gitlab.client

import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration

/**
 * Creates a client for accessing GitLab.
 */
interface OntrackGitLabClientFactory {

    /**
     * Given a GitLab engine configuration, creates a GitLab client.
     */
    fun create(configuration: GitLabConfiguration): OntrackGitLabClient

}

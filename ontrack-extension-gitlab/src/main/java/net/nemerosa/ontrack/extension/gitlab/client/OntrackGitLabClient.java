package net.nemerosa.ontrack.extension.gitlab.client;

import java.util.List;

/**
 * Client used to connect to a GitLab instance from Ontrack.
 */
public interface OntrackGitLabClient {

    /**
     * Gets the list of repositories available using this client.
     */
    List<String> getRepositories();

}

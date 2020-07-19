package net.nemerosa.ontrack.extension.gitlab.client;

import net.nemerosa.ontrack.extension.git.model.GitPullRequest;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Client used to connect to a GitLab instance from Ontrack.
 */
public interface OntrackGitLabClient {

    /**
     * Gets the list of repositories available using this client.
     */
    List<String> getRepositories();

    /**
     * Gets an issue from a repository.
     *
     * @param repository Repository name, like <code>nemerosa/ontrack</code>
     * @param id         ID of the issue
     * @return Details about the issue
     */
    GitLabIssueWrapper getIssue(String repository, int id);

    /**
     * Gets a pull request using its ID
     *
     * @param repository Repository name, like <code>nemerosa/ontrack</code>
     * @param id         ID of the pull request
     * @return Details of the pull request
     */
    @Nullable
    GitPullRequest getPullRequest(@NotNull String repository, int id);

}

package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.git.model.GitSynchronisationStatus;

import java.util.List;

/**
 * Information about a Git repository and the ways to synchronise it.
 */
@Data
public class GitSynchronisationInfo {

    /**
     * Type of Git provided (github, basic, etc.)
     *
     * @see GitConfiguration
     */
    private final String type;

    /**
     * Name of the configuration
     */
    private final String name;

    /**
     * Remote URL
     */
    private final String remote;

    /**
     * Indexation interval
     */
    private final int indexationInterval;

    /**
     * General status
     */
    private final GitSynchronisationStatus status;

    /**
     * Synchronisation status, index of commits per branch. If no branch is present, it
     * means that the repository was never synched.
     */
    private final List<GitSynchronisationInfoBranch> branches;

}

package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;

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
     * TODO Synchronisation status
     */

}

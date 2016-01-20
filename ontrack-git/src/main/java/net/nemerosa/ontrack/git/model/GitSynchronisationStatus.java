package net.nemerosa.ontrack.git.model;

/**
 * General status for the synchronisation.
 */
public enum GitSynchronisationStatus {

    /**
     * There is a repository
     */
    IDLE,

    /**
     * The synchronisation is currently running
     */
    RUNNING,

    /**
     * No synchronisation was ever done
     */
    NONE
}

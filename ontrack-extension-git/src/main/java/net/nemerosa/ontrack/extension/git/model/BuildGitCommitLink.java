package net.nemerosa.ontrack.extension.git.model;

/**
 * Defines the way to link builds to Git commits, in order to manage the change logs, the Git searches
 * and synchronisations.
 *
 * @param <T> Type of configuration data
 */
public interface BuildGitCommitLink<T> {

    /**
     * ID of the link
     */
    String getId();

    /**
     * Display name for the link
     */
    String getName();
}

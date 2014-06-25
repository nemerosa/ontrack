package net.nemerosa.ontrack.extension.svn.model;

/**
 * Type of change on a file.
 */
public enum SVNChangeLogFileChangeType {

    /**
     * The file has been added
     */
    ADDED,

    /**
     * The file has been modified
     */
    MODIFIED,

    /**
     * The file has been deleted
     */
    DELETED,

    /**
     * Change not managed
     */
    UNDEFINED

}

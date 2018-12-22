package net.nemerosa.ontrack.extension.scm.model

/**
 * Type of change on a file.
 */
enum class SCMChangeLogFileChangeType {

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
     * Renaming
     */
    RENAMED,

    /**
     * Copy
     */
    COPIED,

    /**
     * Change not managed
     */
    UNDEFINED

}

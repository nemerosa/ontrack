package net.nemerosa.ontrack.extension.scm.model

interface SCMChangeLogFile {

    /**
     * Gets the reference path
     */
    val path: String

    /**
     * Change types
     */
    val changeTypes: List<SCMChangeLogFileChangeType>

}

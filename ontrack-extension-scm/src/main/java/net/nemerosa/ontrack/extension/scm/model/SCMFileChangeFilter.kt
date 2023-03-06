package net.nemerosa.ontrack.extension.scm.model

/**
 * Defines a filter on the file changes.
 */
data class SCMFileChangeFilter(
    /**
     * Name of the filter
     */
    val name: String,
    /**
     * List of ANT-like patterns for the paths
     */
    val patterns: List<String>,
)
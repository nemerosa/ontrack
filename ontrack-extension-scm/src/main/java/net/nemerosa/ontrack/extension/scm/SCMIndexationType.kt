package net.nemerosa.ontrack.extension.scm

/**
 * Type of indexation for SCM entries (commits & issues).
 */
enum class SCMIndexationType {
    /**
     * Elastic search indexation (legacy)
     */
    ELASTIC_SEARCH,

    /**
     * Database indexation (experimental)
     */
    DATABASE,
}
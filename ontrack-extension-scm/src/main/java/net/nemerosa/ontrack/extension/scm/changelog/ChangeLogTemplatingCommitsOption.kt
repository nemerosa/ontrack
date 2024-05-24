package net.nemerosa.ontrack.extension.scm.changelog

/**
 * Defines how to render commits for a change log
 */
enum class ChangeLogTemplatingCommitsOption {

    /**
     * Never rendering the commits (the default)
     */
    NONE,

    /**
     * Only rendering the commits if no issue is present
     */
    OPTIONAL,

    /**
     * Always rendering the commits
     */
    ALWAYS,

}
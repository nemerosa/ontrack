package net.nemerosa.ontrack.extension.git.model

/**
 * Options for loading the commits in a change log.
 */
data class GitChangeLogCommitOptions(
    var showBuilds: Boolean = false,
    var showPromotions: Boolean = false,
    var showDependencies: Boolean = false,
)
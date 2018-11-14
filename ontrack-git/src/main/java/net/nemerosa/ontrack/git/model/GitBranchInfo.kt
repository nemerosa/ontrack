package net.nemerosa.ontrack.git.model

/**
 * Information about a branch in a Git repository.
 *
 * @property name Name of the branch
 * @property commit Last commit on the branch
 */
class GitBranchInfo(
        val name: String,
        val commit: GitCommit
)

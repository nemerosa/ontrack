package net.nemerosa.ontrack.git.model

/**
 * Information about the branches in a Git repository.
 */
class GitBranchesInfo(
        val branches: List<GitBranchInfo>
) {
    companion object {
        @JvmStatic
        fun empty(): GitBranchesInfo {
            return GitBranchesInfo(emptyList())
        }
    }
}

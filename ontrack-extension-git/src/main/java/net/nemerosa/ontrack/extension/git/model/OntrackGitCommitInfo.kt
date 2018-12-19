package net.nemerosa.ontrack.extension.git.model

/**
 * All the information about a commit in a Git configuration, with its links with all
 * the projects.
 *
 * @property uiCommit Basic info about the commit
 * @property branchInfos Informations per category of branches
 */
class OntrackGitCommitInfo(
        val uiCommit: GitUICommit,
        val branchInfos: Map<String, List<BranchInfo>>
) {
    /**
     * Keeps only the first branch per type
     */
    fun first() = OntrackGitCommitInfo(
            uiCommit,
            branchInfos.mapValues { (_, branchInfoList) ->
                branchInfoList.take(1)
            }
    )
}

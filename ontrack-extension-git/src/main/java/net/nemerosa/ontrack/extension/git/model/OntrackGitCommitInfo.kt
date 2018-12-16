package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.structure.Build

/**
 * All the information about a commit in a Git configuration, with its links with all
 * the projects.
 *
 * @property uiCommit Basic info about the commit
 * @property firstBuild First build after the commit
 */
class OntrackGitCommitInfo(
        val uiCommit: GitUICommit,
        val firstBuild: Build?,
        val branchInfos: Map<String, List<BranchInfo>>
) {
    /**
     * Keeps only the first branch per type
     */
    fun first() = OntrackGitCommitInfo(
            uiCommit,
            firstBuild,
            branchInfos.mapValues { (_, branchInfoList) ->
                branchInfoList.take(1)
            }
    )
}

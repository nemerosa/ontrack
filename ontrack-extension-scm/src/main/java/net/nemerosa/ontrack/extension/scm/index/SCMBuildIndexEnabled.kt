package net.nemerosa.ontrack.extension.scm.index

import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build

/**
 * Marker interface for [SCM] which allows the indexation of commits per builds.
 */
interface SCMBuildIndexEnabled : SCM {

    /**
     * Gets the list of all branches which contain the given commit.
     *
     * @param commit The commit SHA
     * @return List of branch names (simple names, like `main` and not `refs/heads/main`)
     */
    fun getBranchesForCommit(commit: String): List<String>

    /**
     * Given a commit on a branch, returns the earliest build which contains this commit.
     *
     * @param branch The build must be part of this branch
     * @param commit Commit SHA to find
     * @return The earliest build which contains this commit or null
     */
    fun findEarliestBuildAfterCommit(branch: Branch, commit: String): Build?
}
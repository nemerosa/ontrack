package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

/**
 * This checks a branch its [staleness status][StaleBranchStatus].
 */
interface StaleBranchCheck : Extension {

    /**
     * Is this [project] eligible for a check?
     */
    fun isProjectEligible(project: Project): Boolean

    /**
     * Is this [branch] eligible for a check?
     */
    fun isBranchEligible(branch: Branch): Boolean

    /**
     * Checks the staleness of the [branch]
     *
     * @param branch Branch to check
     * @param lastBuild Last build on this branch, if any
     * @return The staleness status, or `null` if this check does not emit any advice
     */
    fun getBranchStaleness(branch: Branch, lastBuild: Build?): StaleBranchStatus?

}
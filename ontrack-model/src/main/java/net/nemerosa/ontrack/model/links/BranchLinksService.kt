package net.nemerosa.ontrack.model.links

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchLink

/**
 * Service to compute links between branches.
 */
interface BranchLinksService {

    /**
     * Given a [branch], returns its downstream links based on the first [n] builds.
     *
     * @param branch Source branch
     * @param n Number of builds to take into account to get the branch dependencies
     * @return Downstream dependencies (ie. the branches [branch] depends on)
     */
    fun getDownstreamDependencies(
        branch: Branch,
        n: Int,
    ): List<BranchLink>

    /**
     * Given a [branch], returns its upstream links based on the first [n] builds.
     *
     * @param branch Source branch
     * @param n Number of builds to take into account to get the branch upstream dependencies
     * @return Upstream dependencies (ie. the branches [branch] is a dependency of)
     */
    fun getUpstreamDependencies(
        branch: Branch,
        n: Int,
    ): List<BranchLink>

}
package net.nemerosa.ontrack.model.links

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchLink
import net.nemerosa.ontrack.model.structure.Build

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

    /**
     * Given a starting [branch] and a [direction], computes the abstract graph of links starting
     * from this branch.
     *
     * @param branch Branch to start from
     * @param direction Direction to follow for the build links
     * @return Node for this branch (never `null` but might have no edges)
     */
    @Deprecated("use the non recursive versions. Will be removed in V5.")
    fun getBranchLinks(branch: Branch, direction: BranchLinksDirection): BranchLinksNode

    /**
     * Given a starting [build] and a [direction], computes the graph of links starting
     * from this build.
     *
     * The [abstract graph][getBranchLinks] is computed first and is used as a _skeleton_ for the
     * build graph.
     *
     * @param build Build to start from
     * @param direction Direction to follow for the build links
     * @return Node for this build
     */
    @Deprecated("use the non recursive versions. Will be removed in V5.")
    fun getBuildLinks(build: Build, direction: BranchLinksDirection): BranchLinksNode

}
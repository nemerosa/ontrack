package net.nemerosa.ontrack.model.links

import net.nemerosa.ontrack.model.structure.Branch

/**
 * Service to compute links between branches.
 */
interface BranchLinksService {

    /**
     * Given a starting [branch] and a [direction], computes the abstract graph of links starting
     * from this branch.
     *
     * @param branch Branch to start from
     * @param direction Direction to follow for the build links
     * @return Node for this branch (never `null` but might have no edges)
     */
    fun getBranchLinks(branch: Branch, direction: BranchLinksDirection): BranchLinksNode

    /**
     * Given a starting [build] and a [direction], computes the graph of links starting
     * from this build.
     *
     * The [abstract graph][getBranchLinks] is computed first and is used as a _skeleton_ for the
     * build graph.
     *
     * @param Build Build to start from
     * @param direction Direction to follow for the build links
     * @return Node for this build
     */
    fun getBuildLinks(branch: Branch, direction: BranchLinksDirection): BranchLinksNode

}
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

}
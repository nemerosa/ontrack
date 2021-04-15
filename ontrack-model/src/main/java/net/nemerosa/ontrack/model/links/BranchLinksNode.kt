package net.nemerosa.ontrack.model.links

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build

/**
 * Node in the graph of links for a branch.
 *
 * @property branch Branch associated to this node
 * @property build Actual build for this node
 * @property edges Edges coming out of this node to other nodes
 */
class BranchLinksNode(
    val branch: Branch,
    val build: Build?,
    val edges: List<BranchLinksEdge>
)

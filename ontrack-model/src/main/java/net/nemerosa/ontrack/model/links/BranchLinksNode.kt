package net.nemerosa.ontrack.model.links

import net.nemerosa.ontrack.model.structure.Branch

/**
 * Node in the graph of links for a branch.
 *
 * @property branch Branch associated to this node
 * @property edges Edges coming out of this node to other nodes
 */
class BranchLinksNode(
    val branch: Branch,
    val edges: List<BranchLinksEdge>
)

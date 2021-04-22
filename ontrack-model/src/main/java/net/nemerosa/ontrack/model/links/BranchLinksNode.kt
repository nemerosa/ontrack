package net.nemerosa.ontrack.model.links

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build

/**
 * Node in the graph of links for a branch.
 *
 * @property branch Branch associated to this node
 * @property build Actual build for this node
 * @property edges Edges coming out of this node to other nodes
 */
@APIDescription("Node in the graph of links for a branch.")
class BranchLinksNode(
    @APIDescription("Branch associated to this node")
    val branch: Branch,
    @APIDescription("Actual build for this node")
    val build: Build?,
    @APIDescription("Edges coming out of this node to other nodes")
    val edges: List<BranchLinksEdge>
)

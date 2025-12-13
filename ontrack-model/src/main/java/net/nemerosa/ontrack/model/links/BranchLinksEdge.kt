package net.nemerosa.ontrack.model.links

/**
 * Edge between two [nodes][BranchLinksNode] in a graph of branch links.
 *
 * @property direction Edge direction (dependency relationship)
 * @property linkedTo Target of the edge
 */
class BranchLinksEdge(
    val direction: BranchLinksDirection,
    val linkedTo: BranchLinksNode,
)
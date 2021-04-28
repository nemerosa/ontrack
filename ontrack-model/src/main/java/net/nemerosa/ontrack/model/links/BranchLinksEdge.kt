package net.nemerosa.ontrack.model.links

/**
 * Edge between two [nodes][BranchLinksNode] in a graph of branch links.
 *
 * @property direction Edge direction (dependency relationship)
 * @property linkedTo Target of the edge
 * @property decorations Decorations for this edge
 */
class BranchLinksEdge(
    val direction: BranchLinksDirection,
    val linkedTo: BranchLinksNode,
    val decorations: List<BranchLinksDecoration>
)
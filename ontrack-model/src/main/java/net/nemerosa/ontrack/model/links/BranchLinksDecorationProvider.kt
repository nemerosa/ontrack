package net.nemerosa.ontrack.model.links

/**
 * Given two nodes in a graph of branch links, provides a decoration for the edge.
 */
interface BranchLinksDecorationProvider {

    /**
     * ID for this provider
     */
    val id: String

    /**
     * Given two nodes, [source] and [target], linked in a given [direction], returns, if any, a decoration
     * for the edge.
     *
     * @param source Source of the link
     * @param target Target of the link
     * @param direction Direction of the link
     * @return Decoration if any, `null` otherwise
     */
    fun getDecoration(
        source: BranchLinksNode,
        target: BranchLinksNode,
        direction: BranchLinksDirection
    ): BranchLinksDecoration?

}
package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.links.BranchLinksDecoration
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import net.nemerosa.ontrack.model.links.BranchLinksNode

/**
 * Extension to enrich edge decorations in build graph.
 */
interface BranchLinksDecorationExtension : Extension {

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
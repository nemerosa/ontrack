package net.nemerosa.ontrack.service.links.decorations

import net.nemerosa.ontrack.model.links.BranchLinksDecoration
import net.nemerosa.ontrack.model.links.BranchLinksDecorationProvider
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import net.nemerosa.ontrack.model.links.BranchLinksNode
import org.springframework.stereotype.Component

@Component
class LinkBranchLinksDecorationProvider : BranchLinksDecorationProvider {

    override val id: String = "link"

    override fun getDecoration(
        source: BranchLinksNode,
        target: BranchLinksNode,
        direction: BranchLinksDirection
    ): BranchLinksDecoration? =
        if (source.build != null && target.build != null) {
            BranchLinksDecoration(
                id = id,
                text = "Linked",
                description = "A link is present between those two builds"
            )
        } else {
            null
        }
}
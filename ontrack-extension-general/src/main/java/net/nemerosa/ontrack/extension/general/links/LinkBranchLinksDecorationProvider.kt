package net.nemerosa.ontrack.extension.general.links

import net.nemerosa.ontrack.extension.api.BranchLinksDecorationExtension
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.links.BranchLinksDecoration
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import net.nemerosa.ontrack.model.links.BranchLinksNode
import org.springframework.stereotype.Component

@Component
class LinkBranchLinksDecorationProvider(
    val extensionFeature: GeneralExtensionFeature
) : AbstractExtension(extensionFeature), BranchLinksDecorationExtension {

    override val id: String = "link"

    override fun getDecoration(
        source: BranchLinksNode,
        target: BranchLinksNode,
        direction: BranchLinksDirection
    ): BranchLinksDecoration? =
        if (source.build != null && target.build != null) {
            BranchLinksDecoration(
                feature = extensionFeature.featureDescription,
                id = id,
                text = "",
                description = "A link is present between those two builds",
                icon = "link"
            )
        } else {
            null
        }
}
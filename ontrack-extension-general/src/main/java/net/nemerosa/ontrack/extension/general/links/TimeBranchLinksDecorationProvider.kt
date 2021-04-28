package net.nemerosa.ontrack.extension.general.links

import net.nemerosa.ontrack.common.formatDuration
import net.nemerosa.ontrack.extension.api.BranchLinksDecorationExtension
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.links.BranchLinksDecoration
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import net.nemerosa.ontrack.model.links.BranchLinksNode
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class TimeBranchLinksDecorationProvider(
    val extensionFeature: GeneralExtensionFeature
) : AbstractExtension(extensionFeature), BranchLinksDecorationExtension {

    override val id: String = "time"

    override fun getDecoration(
        source: BranchLinksNode,
        target: BranchLinksNode,
        direction: BranchLinksDirection
    ): BranchLinksDecoration? =
        if (source.build != null && target.build != null) {
            BranchLinksDecoration(
                feature = extensionFeature.featureDescription,
                id = id,
                text = displayTime(source.build!!, target.build!!),
                description = "Time between the two builds",
                icon = "time"
            )
        } else {
            null
        }

    private fun displayTime(source: Build, target: Build): String {
        val start = source.signature.time
        val end = target.signature.time
        val duration = Duration.between(start, end).abs()
        return formatDuration(duration)
    }
}
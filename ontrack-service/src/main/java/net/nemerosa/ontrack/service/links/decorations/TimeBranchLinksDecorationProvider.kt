package net.nemerosa.ontrack.service.links.decorations

import net.nemerosa.ontrack.model.links.BranchLinksDecoration
import net.nemerosa.ontrack.model.links.BranchLinksDecorationProvider
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import net.nemerosa.ontrack.model.links.BranchLinksNode
import net.nemerosa.ontrack.model.structure.Build
import org.apache.commons.lang3.time.DurationFormatUtils
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class TimeBranchLinksDecorationProvider : BranchLinksDecorationProvider {

    override val id: String = "time"

    override fun getDecoration(
        source: BranchLinksNode,
        target: BranchLinksNode,
        direction: BranchLinksDirection
    ): BranchLinksDecoration? =
        if (source.build != null && target.build != null) {
            BranchLinksDecoration(
                id = id,
                text = displayTime(source.build!!, target.build!!),
                description = "Time between the two builds"
            )
        } else {
            null
        }

    private fun displayTime(source: Build, target: Build): String {
        val start = source.signature.time
        val end = target.signature.time
        val duration = Duration.between(start, end).abs()
        return DurationFormatUtils.formatDurationWords(
            duration.toMillis(),
            true,
            true
        )
    }
}
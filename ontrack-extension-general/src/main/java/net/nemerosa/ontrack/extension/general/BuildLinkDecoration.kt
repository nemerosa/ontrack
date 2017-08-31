package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.PromotionRun
import java.net.URI

class BuildLinkDecoration(
        val project: String,
        val build: String,
        val uri: URI,
        val promotionRuns: List<PromotionRun>
)


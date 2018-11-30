package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.ui.controller.URIBuilder
import java.net.URI

class BuildLinkDecoration(
        val project: String,
        val build: String,
        val label: String,
        val uri: URI,
        val promotions: List<BuildLinkDecorationPromotion>
)

fun Build.asBuildLinkDecoration(uriBuilder: URIBuilder, promotionRuns: List<PromotionRun>, label: String) =
        BuildLinkDecoration(
                this.project.name,
                this.name,
                label,
                uriBuilder.getEntityPage(this),
                promotionRuns.map {
                    BuildLinkDecorationPromotion(
                            it.promotionLevel.name,
                            // See PromotionLevelController
                            uriBuilder.url(
                                    "/structure/promotionLevels/${it.promotionLevel.id}/image"
                            ).toString()
                    )
                }
        )
package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.BuildLink
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.ui.controller.EntityURIBuilder
import java.net.URI

/**
 * List of build links, plus an indicator (link to the build page)
 * showing if there is extra links.
 */
class BuildLinkDecorationList(
        val decorations: List<BuildLinkDecoration>,
        val extraLink: URI?
)

class BuildLinkDecoration(
        val project: String,
        val build: String,
        val label: String,
        val qualifier: String,
        val uri: URI,
        val promotions: List<BuildLinkDecorationPromotion>
)

fun BuildLink.asBuildLinkDecoration(uriBuilder: EntityURIBuilder, promotionRuns: List<PromotionRun>, label: String) =
        BuildLinkDecoration(
                project = this.build.project.name,
                build = this.build.name,
                label = label,
                qualifier = this.qualifier,
                uri = uriBuilder.getEntityPage(this.build),
                promotions = promotionRuns.map {
                    BuildLinkDecorationPromotion(
                            it.promotionLevel.name,
                            // See PromotionLevelController
                            uriBuilder.url(
                                    "/rest/structure/promotionLevels/${it.promotionLevel.id}/image"
                            ).toString()
                    )
                }
        )
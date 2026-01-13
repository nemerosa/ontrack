package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig
import org.springframework.stereotype.Service

@Service
class SemanticPromotionChangeLogTemplatingServiceImpl(
    structureService: StructureService,
    buildFilterService: BuildFilterService,
    private val semanticChangeLogTemplatingService: SemanticChangeLogTemplatingService,
) : AbstractPromotionChangeLogTemplatingService(structureService, buildFilterService),
    SemanticPromotionChangeLogTemplatingService {

    override fun render(
        toBuild: Build,
        promotion: String,
        config: TemplatingSourceConfig,
        renderer: EventRenderer
    ): String {

        val acrossBranches = config.getBoolean(
            SemanticPromotionChangeLogTemplatingServiceConfig::acrossBranches.name,
            true
        )

        val fromBuild = promotionBoundaries(toBuild, promotion, acrossBranches) ?: return ""

        val config = config.parse<SemanticPromotionChangeLogTemplatingServiceConfig>()
        return semanticChangeLogTemplatingService.render(
            fromBuild = fromBuild,
            toBuild = toBuild,
            config = config,
            renderer = renderer
        )
    }

}
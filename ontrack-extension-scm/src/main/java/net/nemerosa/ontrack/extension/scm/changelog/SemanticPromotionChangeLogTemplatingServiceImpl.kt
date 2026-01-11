package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.templating.getBooleanTemplatingParam
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
        configMap: Map<String, String>,
        renderer: EventRenderer
    ): String {

        val acrossBranches = configMap.getBooleanTemplatingParam(
            SemanticPromotionChangeLogTemplatingServiceConfig::acrossBranches.name,
            true
        )

        val fromBuild = promotionBoundaries(toBuild, promotion, acrossBranches) ?: return ""

        val config = configMap.asJson().parse<SemanticPromotionChangeLogTemplatingServiceConfig>()
        return semanticChangeLogTemplatingService.render(
            fromBuild = fromBuild,
            toBuild = toBuild,
            config = config,
            renderer = renderer
        )
    }

}
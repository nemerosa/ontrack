package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.extension.scm.service.PromotionRunChangeLogTemplatingSourceConfig
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig
import org.springframework.stereotype.Service

@Service
class PromotionChangeLogTemplatingServiceImpl(
    buildFilterService: BuildFilterService,
    structureService: StructureService,
    private val changeLogTemplatingService: ChangeLogTemplatingService,
) : AbstractPromotionChangeLogTemplatingService(structureService, buildFilterService),
    PromotionChangeLogTemplatingService {

    override fun render(
        toBuild: Build,
        promotion: String,
        config: TemplatingSourceConfig,
        renderer: EventRenderer
    ): String {

        val acrossBranches = config.getBoolean(
            PromotionRunChangeLogTemplatingSourceConfig::acrossBranches.name,
            true
        )

        val fromBuild = promotionBoundaries(
            toBuild = toBuild,
            promotion = promotion,
            acrossBranches = acrossBranches,
        ) ?: return ChangeLogTemplatingServiceConfig.emptyValue(config)

        val config = config.parse<ChangeLogTemplatingServiceConfig>()

        return changeLogTemplatingService.render(
            fromBuild = fromBuild,
            toBuild = toBuild,
            config = config,
            renderer = renderer
        )
    }

}
package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingServiceConfig
import net.nemerosa.ontrack.extension.scm.changelog.PromotionChangeLogTemplatingService
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.docs.DocumentationQualifier
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.templating.AbstractTemplatingSource
import org.springframework.stereotype.Component

@Component
@APIDescription(
    """
    Renders a change log for this promotion run.
    
    The "to build" is the one being promoted.
     
    The "from build" is the last build (before this one) having been promoted to the associated
    promotion level.
    
    If no such previous build is found on the associated branch, the search will be done
    across the whole project, unless the `acrossBranches` configuration parameter is set to `false`.
    
    If `project` is set to a comma-separated list of strings, the change log will be rendered 
    for the recursive links, in the order to the projects being set (going deeper and deeper
    in the links). 
"""
)
@Documentation(PromotionRunChangeLogTemplatingSourceConfig::class)
@DocumentationQualifier("promotion-run", "PromotionRun")
@DocumentationExampleCode("${'$'}{promotionRun.changelog}")
class PromotionRunChangeLogTemplatingSource(
    private val promotionChangeLogTemplatingService: PromotionChangeLogTemplatingService,
) : AbstractTemplatingSource(
    field = "changelog",
    type = ProjectEntityType.PROMOTION_RUN,
) {

    override fun render(entity: ProjectEntity, configMap: Map<String, String>, renderer: EventRenderer): String {
        val empty = ChangeLogTemplatingServiceConfig.emptyValue(configMap)
        return if (entity is PromotionRun) {
            promotionChangeLogTemplatingService.render(
                toBuild = entity.build,
                promotion = entity.promotionLevel.name,
                configMap = configMap,
                renderer = renderer,
            )
        } else {
            empty
        }
    }

}
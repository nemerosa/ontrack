package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingService
import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingServiceConfig
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.docs.DocumentationQualifier
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.templating.AbstractTemplatingSource
import net.nemerosa.ontrack.model.templating.getRequiredTemplatingParam
import org.springframework.stereotype.Component

@Component
@APIDescription(
    """
    Renders a change log for this build.
    
    The "to build" is the one being referred to.
     
    The "from build" is the build whose ID is set by the "from" parameter.
    
    If `project` is set to a comma-separated list of strings, the change log will be rendered 
    for the recursive links, in the order to the projects being set (going deeper and deeper
    in the links). 
"""
)
@Documentation(BuildChangeLogTemplatingSourceConfig::class)
@DocumentationQualifier("build", "Build")
@DocumentationExampleCode("${'$'}{build.changelog?from=1}")
class BuildChangeLogTemplatingSource(
    private val changeLogTemplatingService: ChangeLogTemplatingService,
    private val structureService: StructureService,
) : AbstractTemplatingSource(
    field = "changelog",
    type = ProjectEntityType.BUILD,
) {

    override fun render(entity: ProjectEntity, configMap: Map<String, String>, renderer: EventRenderer): String {
        val empty = ChangeLogTemplatingServiceConfig.emptyValue(configMap)
        return if (entity is Build) {
            val fromId = configMap.getRequiredTemplatingParam(BuildChangeLogTemplatingSourceConfig::from.name).toInt()
            val from = structureService.getBuild(ID.of(fromId))
            changeLogTemplatingService.render(
                fromBuild = entity,
                toBuild = from,
                configMap = configMap,
                renderer = renderer,
            )
        } else {
            empty
        }
    }

}
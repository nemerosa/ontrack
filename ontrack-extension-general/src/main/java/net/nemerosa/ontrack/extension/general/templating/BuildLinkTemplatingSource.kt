package net.nemerosa.ontrack.extension.general.templating

import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.templating.AbstractTemplatingSource
import net.nemerosa.ontrack.model.templating.getRequiredTemplatingParam
import org.springframework.stereotype.Component

/**
 * Given a [build][Build], returns the name of a linked build.
 */
@Component
@APIDescription("Getting a linked build and displaying its name or release name.")
@Documentation(BuildLinkTemplatingSourceDocumentation::class)
@DocumentationExampleCode("${'$'}{build.linked?project=dependency&mode=auto}")
class BuildLinkTemplatingSource(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
) : AbstractTemplatingSource(
    field = "linked",
    type = ProjectEntityType.BUILD,
) {
    override fun render(entity: ProjectEntity, configMap: Map<String, String>, renderer: EventRenderer): String =
        if (entity is Build) {
            val project = configMap.getRequiredTemplatingParam("project")
            val qualifier = configMap["qualifier"] ?: ""
            val mode = configMap["mode"]
                ?.let { BuildLinkTemplatingSourceMode.valueOf(it.uppercase()) }
                ?: BuildLinkTemplatingSourceMode.NAME
            val linked = structureService.getQualifiedBuildsUsedBy(
                build = entity,
            ) { candidate ->
                candidate.build.project.name == project && candidate.qualifier == qualifier
            }.pageItems.firstOrNull()
            if (linked != null) {
                val release = propertyService.getPropertyValue(linked.build, ReleasePropertyType::class.java)?.name
                when (mode) {
                    BuildLinkTemplatingSourceMode.NAME -> linked.build.name
                    BuildLinkTemplatingSourceMode.RELEASE -> release
                        ?: throw BuildLinkTemplatingSourceNoReleaseException(linked.build)

                    BuildLinkTemplatingSourceMode.AUTO -> release ?: linked.build.name
                }
            } else {
                ""
            }
        } else {
            ""
        }
}
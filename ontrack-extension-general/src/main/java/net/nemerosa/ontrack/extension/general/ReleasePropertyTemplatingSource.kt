package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.templating.AbstractTemplatingSource
import org.springframework.stereotype.Component

@Component
@APIDescription("Gets the release/version/label associated to a build or renders an empty string is there is none.")
@DocumentationExampleCode("${'$'}{build.release}")
@DocumentationIgnore
class ReleasePropertyTemplatingSource(
    @DocumentationIgnore
    private val propertyService: PropertyService,
) : AbstractTemplatingSource(
    field = "release",
    type = ProjectEntityType.BUILD,
) {

    override fun render(entity: ProjectEntity, configMap: Map<String, String>, renderer: EventRenderer): String =
        if (entity is Build) {
            propertyService.getPropertyValue(entity, ReleasePropertyType::class.java)?.name ?: ""
        } else {
            ""
        }
}
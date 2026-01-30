package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.templating.TemplatingSource
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig
import org.springframework.stereotype.Component

@Component
@APIDescription("Getting the description for an entity.")
@Documentation(EntityDescriptionTemplatingSourceParameters::class)
@DocumentationExampleCode($$"${branch.description}")
class EntityDescriptionTemplatingSource : TemplatingSource {

    override val types: Set<ProjectEntityType> = ProjectEntityType.values().toSet()

    override val field: String = "description"

    override fun render(entity: ProjectEntity, config: TemplatingSourceConfig, renderer: EventRenderer): String {
        return entity.description
            ?.takeIf { it.isNotBlank() }
            ?: config.getString(EntityDescriptionTemplatingSourceParameters::default.name)
            ?: ""
    }
}
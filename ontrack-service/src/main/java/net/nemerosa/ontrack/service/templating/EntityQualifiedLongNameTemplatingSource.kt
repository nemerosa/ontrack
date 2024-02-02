package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.templating.TemplatingSource
import org.springframework.stereotype.Component

@Component
class EntityQualifiedLongNameTemplatingSource : TemplatingSource {

    override fun validFor(projectEntityType: ProjectEntityType): Boolean = true

    override val field: String = "qualifiedLongName"

    override fun render(entity: ProjectEntity, configMap: Map<String, String>, renderer: EventRenderer): String =
        entity.entityDisplayName.replaceFirstChar { it.lowercase() }
}
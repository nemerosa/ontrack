package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.templating.TemplatingSource
import org.springframework.stereotype.Component

@Component
@APIDescription("Getting the qualified long name for an entity. For a branch, it'd look like `branch project/main`.")
@DocumentationExampleCode("${'$'}{branch.qualifiedLongName}")
class EntityQualifiedLongNameTemplatingSource : TemplatingSource {

    override val types: Set<ProjectEntityType> = ProjectEntityType.values().toSet()

    override val field: String = "qualifiedLongName"

    override fun render(entity: ProjectEntity, configMap: Map<String, String>, renderer: EventRenderer): String =
        entity.entityDisplayName.replaceFirstChar { it.lowercase() }
}
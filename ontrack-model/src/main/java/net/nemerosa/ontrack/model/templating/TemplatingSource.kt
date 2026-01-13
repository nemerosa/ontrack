package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType

/**
 * Extends the templating for a project entity.
 */
interface TemplatingSource {

    val types: Set<ProjectEntityType>

    val field: String

    fun render(entity: ProjectEntity, config: TemplatingSourceConfig, renderer: EventRenderer): String

}

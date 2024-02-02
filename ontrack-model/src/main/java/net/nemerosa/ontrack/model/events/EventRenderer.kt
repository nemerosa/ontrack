package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.NameValue

interface EventRenderer {

    @Deprecated("Will be removed in V5. Use render without event.")
    fun render(projectEntity: ProjectEntity, event: Event): String = render(projectEntity)

    /**
     * Rendering the link to an entity.
     */
    fun render(projectEntity: ProjectEntity): String

    fun render(valueKey: String, value: NameValue, event: Event): String

    fun renderLink(text: NameValue, link: NameValue, event: Event): String
}

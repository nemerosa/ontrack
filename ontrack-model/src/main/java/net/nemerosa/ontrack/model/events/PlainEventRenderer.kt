package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.displayName
import net.nemerosa.ontrack.model.support.NameValue

class PlainEventRenderer : AbstractEventRenderer() {

    override fun render(projectEntity: ProjectEntity): String = projectEntity.displayName

    override fun render(valueKey: String, value: NameValue, event: Event): String = value.value

    override fun renderLink(text: String, href: String): String = text

    companion object {
        @JvmField
        val INSTANCE: EventRenderer = PlainEventRenderer()
    }
}
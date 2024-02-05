package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.displayName

class PlainEventRenderer : AbstractEventRenderer() {

    override fun render(projectEntity: ProjectEntity): String = projectEntity.displayName

    override fun renderStrong(value: String): String = value

    override fun renderLink(text: String, href: String): String = text

    override fun renderList(list: List<String>): String =
        list.joinToString("\n") { "* $it" }

    companion object {
        @JvmField
        val INSTANCE: EventRenderer = PlainEventRenderer()
    }
}
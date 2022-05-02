package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.NameValue

class PlainEventRenderer : AbstractEventRenderer() {

    override fun render(projectEntity: ProjectEntity, event: Event): String =
        getProjectEntityName(projectEntity)

    override fun render(valueKey: String, value: NameValue, event: Event): String = value.value

    companion object {
        @JvmField
        val INSTANCE: EventRenderer = PlainEventRenderer()
    }
}
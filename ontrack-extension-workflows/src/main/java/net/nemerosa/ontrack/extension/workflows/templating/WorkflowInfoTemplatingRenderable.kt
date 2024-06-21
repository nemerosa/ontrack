package net.nemerosa.ontrack.extension.workflows.templating

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingRenderable
import net.nemerosa.ontrack.model.templating.TemplatingRenderableFieldNotFoundException
import net.nemerosa.ontrack.model.templating.TemplatingRenderableFieldRequiredException

class WorkflowInfoTemplatingRenderable(
    private val workflowInstance: WorkflowInstance,
) : TemplatingRenderable {

    override fun render(field: String?, configMap: Map<String, String>, renderer: EventRenderer): String =
        if (field.isNullOrBlank()) {
            throw TemplatingRenderableFieldRequiredException()
        } else {
            when (field) {
                "start" -> workflowInstance.startTime?.run { Time.store(this) } ?: ""
                else -> throw TemplatingRenderableFieldNotFoundException(field)
            }
        }

}
package net.nemerosa.ontrack.extension.workflows.templating

import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance

object WorkflowTemplatingContext {

    fun createTemplatingContext(workflowInstance: WorkflowInstance) = mapOf(
        "workflow" to WorkflowTemplatingRenderable(workflowInstance),
        "workflowInfo" to WorkflowInfoTemplatingRenderable(workflowInstance),
    )

}
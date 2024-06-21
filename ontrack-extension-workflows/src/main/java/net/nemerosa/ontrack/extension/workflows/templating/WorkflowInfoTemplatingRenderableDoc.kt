package net.nemerosa.ontrack.extension.workflows.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.templating.TemplatingRenderableDoc
import net.nemerosa.ontrack.model.templating.TemplatingRenderableDocField
import org.springframework.stereotype.Component

@Component
@APIDescription("The `workflowInfo` context is used to access information about the workflow itself, in notifications or other templates rendered in the context of the workflow execution.")
class WorkflowInfoTemplatingRenderableDoc : TemplatingRenderableDoc {

    override val id: String = "workflowInfo"

    override val displayName: String = "Global information about the workflow"

    override val contextName: String = "Workflow"

    override val fields: List<TemplatingRenderableDocField> = listOf(
        TemplatingRenderableDocField(
            name = "start",
            description = "Starting time of the workflow",
            config = null,
        )
    )
}
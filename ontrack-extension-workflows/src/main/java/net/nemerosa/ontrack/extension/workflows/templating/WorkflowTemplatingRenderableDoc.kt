package net.nemerosa.ontrack.extension.workflows.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.templating.TemplatingRenderableDoc
import net.nemerosa.ontrack.model.templating.TemplatingRenderableDocField
import org.springframework.stereotype.Component

@Component
@APIDescription("The `workflow` context is used to access information about the nodes of the workflow, in notifications or other templates rendered in the context of the workflow execution.")
class WorkflowTemplatingRenderableDoc : TemplatingRenderableDoc {

    override val id: String = "workflow"

    override val displayName: String = "Information about the workflow"

    override val contextName: String = "Workflow"

    override val fields: List<TemplatingRenderableDocField> = listOf(
        TemplatingRenderableDocField(
            name = "<node id>",
            description = "Getting information about a node in the current workflow",
            config = WorkflowTemplatingRenderableParameters::class,
        )
    )
}
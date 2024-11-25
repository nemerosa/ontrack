package net.nemerosa.ontrack.extension.workflows.engine.parallel

import net.nemerosa.ontrack.extension.queue.source.QueueSourceExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import org.springframework.stereotype.Component

@Component
class ParallelWorkflowQueueSourceExtension(
    workflowsExtensionFeature: WorkflowsExtensionFeature,
) : AbstractExtension(workflowsExtensionFeature), QueueSourceExtension<WorkflowQueueSourceData> {

    override val id: String = "workflow-parallel"

}
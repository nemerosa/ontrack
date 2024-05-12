package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowFixtures
import net.nemerosa.ontrack.extension.workflows.mock.MockWorkflowNodeExecutor
import java.time.LocalDateTime

object WorkflowInstanceFixtures {

    fun simpleLinear(
        timestamp: LocalDateTime = Time.now(),
    ): WorkflowInstance {
        val workflow = WorkflowFixtures.simpleLinearWorkflow()
        return createInstance(
            workflow = workflow,
            context = WorkflowContext.noContext(),
            timestamp = timestamp,
        )
    }
}
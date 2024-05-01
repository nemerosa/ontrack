package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowFixtures
import net.nemerosa.ontrack.extension.workflows.mock.MockWorkflowNodeExecutor

object WorkflowInstanceFixtures {

    private val mockWorkflowNodeExecutor = MockWorkflowNodeExecutor(
        WorkflowsExtensionFeature()
    )

    fun simpleLinear(): WorkflowInstance {
        val workflow = WorkflowFixtures.simpleLinearWorkflow()
        return createInstance(workflow, mockWorkflowNodeExecutor)
    }
}
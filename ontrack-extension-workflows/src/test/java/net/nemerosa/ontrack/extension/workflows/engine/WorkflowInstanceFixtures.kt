package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowFixtures
import net.nemerosa.ontrack.model.events.MockEventType
import net.nemerosa.ontrack.model.trigger.TestTrigger
import net.nemerosa.ontrack.model.trigger.TestTriggerData
import net.nemerosa.ontrack.model.trigger.createTriggerData
import java.time.LocalDateTime

object WorkflowInstanceFixtures {

    fun simpleLinear(
        timestamp: LocalDateTime = Time.now(),
    ): WorkflowInstance {
        val workflow = WorkflowFixtures.simpleLinearWorkflow()
        // Event
        val event = MockEventType.serializedMockEvent("Some text")
        return createInstance(
            workflow = workflow,
            event = event,
            triggerData = TestTrigger().createTriggerData(TestTriggerData()),
            timestamp = timestamp,
        )
    }
}
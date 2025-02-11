package net.nemerosa.ontrack.extension.workflows

import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.trigger.TestTrigger
import net.nemerosa.ontrack.model.trigger.TestTriggerData
import net.nemerosa.ontrack.model.trigger.createTriggerData
import org.springframework.beans.factory.annotation.Autowired

@QueueNoAsync
abstract class AbstractWorkflowTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var workflowTestSupport: WorkflowTestSupport

    @Autowired
    private lateinit var testTrigger: TestTrigger

    protected fun testTriggerData(message: String? = null) =
        testTrigger.createTriggerData(
            TestTriggerData(message = message)
        )

}
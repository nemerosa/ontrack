package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.extension.workflows.WorkflowTestSupport
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "net.nemerosa.ontrack.extension.workflows.store=memory",
        "ontrack.extension.queue.general.async=false",
    ]
)
abstract class AbstractWorkflowTestSupport : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var workflowTestSupport: WorkflowTestSupport

}
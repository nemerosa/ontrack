package net.nemerosa.ontrack.extension.workflows

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "net.nemerosa.ontrack.extension.workflows.store=memory",
        "ontrack.extension.queue.general.async=false",
    ]
)
abstract class AbstractWorkflowTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var workflowTestSupport: WorkflowTestSupport

}
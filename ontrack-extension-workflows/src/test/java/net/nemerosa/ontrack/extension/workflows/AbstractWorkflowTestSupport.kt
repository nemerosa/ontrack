package net.nemerosa.ontrack.extension.workflows

import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.springframework.beans.factory.annotation.Autowired

@QueueNoAsync
abstract class AbstractWorkflowTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var workflowTestSupport: WorkflowTestSupport

}
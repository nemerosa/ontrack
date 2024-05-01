package net.nemerosa.ontrack.extension.workflows.engine

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
@ConditionalOnProperty(
    prefix = "net.nemerosa.ontrack.extension.workflows",
    name = ["store"],
    havingValue = "database",
    matchIfMissing = true,
)
@Transactional(propagation = Propagation.REQUIRES_NEW)
class DatabaseWorkflowInstanceStore : WorkflowInstanceStore {

    override fun store(instance: WorkflowInstance) {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): WorkflowInstance? {
        TODO("Not yet implemented")
    }

}
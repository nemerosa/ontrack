package net.nemerosa.ontrack.extension.workflows.engine

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "net.nemerosa.ontrack.extension.workflows",
    name = ["store"],
    havingValue = "memory",
    matchIfMissing = false,
)
class InMemoryWorkflowInstanceStore : WorkflowInstanceStore {

    private val instances = mutableMapOf<String, WorkflowInstance>()

    override fun store(instance: WorkflowInstance): WorkflowInstance {
        instances[instance.id] = instance
        return instance
    }

    override fun findById(id: String): WorkflowInstance? = instances[id]
}
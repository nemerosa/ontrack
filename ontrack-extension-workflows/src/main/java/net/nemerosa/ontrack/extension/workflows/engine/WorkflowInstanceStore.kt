package net.nemerosa.ontrack.extension.workflows.engine

interface WorkflowInstanceStore {

    fun store(instance: WorkflowInstance)

    fun findById(id: String): WorkflowInstance?

}
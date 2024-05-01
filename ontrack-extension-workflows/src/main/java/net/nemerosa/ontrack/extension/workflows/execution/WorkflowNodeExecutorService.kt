package net.nemerosa.ontrack.extension.workflows.execution

interface WorkflowNodeExecutorService {

    fun getExecutor(executorId: String): WorkflowNodeExecutor

}
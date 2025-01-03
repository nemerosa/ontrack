package net.nemerosa.ontrack.extension.workflows.execution

interface WorkflowNodeExecutorService {

    val executors: List<WorkflowNodeExecutor>

    fun getExecutor(executorId: String): WorkflowNodeExecutor

    fun findExecutor(executorId: String): WorkflowNodeExecutor?

}
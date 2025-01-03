package net.nemerosa.ontrack.extension.workflows.execution

import net.nemerosa.ontrack.extension.api.ExtensionManager
import org.springframework.stereotype.Service

@Service
class WorkflowNodeExecutorServiceImpl(
    private val extensionManager: ExtensionManager,
) : WorkflowNodeExecutorService {

    override val executors: List<WorkflowNodeExecutor> by lazy {
        extensionManager.getExtensions(WorkflowNodeExecutor::class.java).sortedBy { it.displayName }
    }

    override fun findExecutor(executorId: String): WorkflowNodeExecutor? =
        executors.find { it.id == executorId }

    override fun getExecutor(executorId: String): WorkflowNodeExecutor =
        findExecutor(executorId)
            ?: throw WorkflowNodeExecutorNotFoundException(executorId)

}
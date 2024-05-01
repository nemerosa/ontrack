package net.nemerosa.ontrack.extension.workflows.execution

import net.nemerosa.ontrack.extension.api.ExtensionManager
import org.springframework.stereotype.Service

@Service
class WorkflowNodeExecutorServiceImpl(
    private val extensionManager: ExtensionManager,
) : WorkflowNodeExecutorService {

    private val executors: Collection<WorkflowNodeExecutor> by lazy {
        extensionManager.getExtensions(WorkflowNodeExecutor::class.java)
    }

    override fun getExecutor(executorId: String): WorkflowNodeExecutor =
        executors.find { it.id == executorId }
            ?: throw WorkflowNodeExecutorNotFoundException(executorId)

}
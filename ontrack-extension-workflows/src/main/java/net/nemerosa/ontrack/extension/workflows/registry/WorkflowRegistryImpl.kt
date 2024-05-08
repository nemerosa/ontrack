package net.nemerosa.ontrack.extension.workflows.registry

import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import java.util.*

@Service
class WorkflowRegistryImpl(
    private val storageService: StorageService,
) : WorkflowRegistry {

    companion object {
        private val STORE = WorkflowRegistry::class.java.name
    }

    override fun saveYamlWorkflow(workflow: String): String {
        // Parsing of the workflow
        val workflowObj: Workflow = WorkflowYaml.parseYamlWorkflow(workflow)
        // Generating an ID
        val id = UUID.randomUUID().toString()
        // Record to save
        val record = InternalRecord(
            workflow = workflowObj,
        )
        // Saving the record
        storageService.store(STORE, id, record)
        // OK
        return id
    }

    override fun findWorkflow(workflowId: String): WorkflowRecord? =
        storageService.find(STORE, workflowId, InternalRecord::class)
            ?.run {
                WorkflowRecord(
                    id = workflowId,
                    workflow = workflow,
                )
            }

    private data class InternalRecord(
        val workflow: Workflow,
    )
}
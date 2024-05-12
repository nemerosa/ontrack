package net.nemerosa.ontrack.extension.workflows.registry

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation
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

    override fun validateJsonWorkflow(workflow: JsonNode): WorkflowValidation {
        // Parsing of the workflow
        val workflowObj: Workflow = try {
            WorkflowParser.parseJsonWorkflow(workflow)
        } catch (ex: Exception) {
            return WorkflowValidation.error(ex)
        }
        // Validation
        return validateWorkflow(workflowObj)
    }

    private fun validateWorkflow(workflow: Workflow): WorkflowValidation {
        // Name is required
        if (workflow.name.isBlank()) {
            return WorkflowValidation.error("Workflow name is required.")
        }
        // One node required
        if (workflow.nodes.isEmpty()) {
            return WorkflowValidation.error("At least one node is required.")
        }
        // Cycle detection
        if (isCyclic(workflow.nodes)) {
            return WorkflowValidation.error("The workflow contains at least one cycle.")
        }
        // OK
        return WorkflowValidation.ok()
    }

    fun isCyclic(nodes: List<WorkflowNode>): Boolean {
        val visited = mutableSetOf<String>()
        val recStack = mutableSetOf<String>()

        fun dfs(nodeId: String): Boolean {
            if (recStack.contains(nodeId)) return true
            if (visited.contains(nodeId)) return false

            visited.add(nodeId)
            recStack.add(nodeId)

            nodes.find { it.id == nodeId }?.parents?.forEach { parent ->
                if (dfs(parent.id)) return true
            }

            recStack.remove(nodeId)
            return false
        }

        return nodes.any { dfs(it.id) }
    }

    override fun saveYamlWorkflow(workflow: String): String {
        // Parsing of the workflow
        val workflowObj: Workflow = WorkflowParser.parseYamlWorkflow(workflow)
        // Validation
        validateWorkflow(workflowObj).throwErrorIfAny()
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
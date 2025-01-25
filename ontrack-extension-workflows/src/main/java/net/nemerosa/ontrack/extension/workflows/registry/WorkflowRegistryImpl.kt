package net.nemerosa.ontrack.extension.workflows.registry

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.acl.WorkflowRegistration
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation.Companion.validateWorkflow
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import java.util.*

@Service
class WorkflowRegistryImpl(
    private val storageService: StorageService,
    private val securityService: SecurityService,
) : WorkflowRegistry {

    companion object {
        private val STORE = WorkflowRegistry::class.java.name
    }

    override fun validateJsonWorkflow(workflow: JsonNode): WorkflowValidation {
        // Parsing of the workflow
        val workflowObj: Workflow = try {
            WorkflowParser.parseJsonWorkflow(workflow)
        } catch (ex: Exception) {
            val name = workflow.path("name").asText()
            if (name.isNullOrBlank()) {
                return WorkflowValidation.unnamedError(ex)
            } else {
                return WorkflowValidation.error(name, ex)
            }
        }
        // Validation
        return validateWorkflow(workflowObj)
    }

    override fun saveYamlWorkflow(workflow: String): String {
        // Checking the access right
        securityService.checkGlobalFunction(WorkflowRegistration::class.java)
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

    override fun findWorkflow(workflowId: String): WorkflowRecord? {
        securityService.checkGlobalFunction(WorkflowRegistration::class.java)
        return storageService.find(STORE, workflowId, InternalRecord::class)
            ?.run {
                WorkflowRecord(
                    id = workflowId,
                    workflow = workflow,
                )
            }
    }

    private data class InternalRecord(
        val workflow: Workflow,
    )
}
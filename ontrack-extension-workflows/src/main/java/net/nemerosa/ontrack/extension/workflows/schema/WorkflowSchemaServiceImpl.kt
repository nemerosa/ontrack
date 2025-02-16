package net.nemerosa.ontrack.extension.workflows.schema

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.json.schema.jsonSchema
import net.nemerosa.ontrack.model.support.EnvService
import org.springframework.stereotype.Service
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.starProjectedType

@Service
class WorkflowSchemaServiceImpl(
    private val envService: EnvService,
    private val workflowNodeExecutors: List<WorkflowNodeExecutor>,
) : WorkflowSchemaService {

    override fun createJsonSchema(): JsonNode =
        jsonSchema(
            id = "https://ontrack.run/${envService.version.display}/schema/workflow",
            title = "Ontrack CasC",
            description = "Configuration as code for Ontrack",
            root = Workflow::class,
        ) {
            idConfig(
                idProperty = WorkflowNode::executorId,
                dataProperty = WorkflowNode::data,
                types = workflowNodeExecutors,
                typeBuilder = { type, toSchema ->
                    val docClass = type::class.findAnnotation<Documentation>()
                        ?: error("$type does not have a Documentation annotation")
                    toSchema(
                        docClass.value.starProjectedType,
                        getAPITypeDescription(type::class)
                    ).asJson()
                },
                typeId = { it.id },
                typeRef = { "workflow-node-executor-${it.id}" },
            )
        }

}
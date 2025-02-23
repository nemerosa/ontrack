package net.nemerosa.ontrack.extension.workflows.schema

import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.json.schema.DynamicJsonSchemaProvider
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.springframework.stereotype.Component
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.starProjectedType

@Component
class WorkflowDynamicJsonSchemaProvider(
    private val workflowNodeExecutors: List<WorkflowNodeExecutor>,
) : DynamicJsonSchemaProvider {

    override val discriminatorValues: List<String>
        get() = workflowNodeExecutors.map { it.id }

    override fun getConfigurationTypes(builder: JsonTypeBuilder): Map<String, JsonType> =
        workflowNodeExecutors.associate { executor ->
            executor.id to getConfigurationType(executor, builder)
        }

    override fun toRef(id: String): String = "workflow-node-executor-$id"

    private fun getConfigurationType(
        executor: WorkflowNodeExecutor,
        builder: JsonTypeBuilder,
    ): JsonType {
        val docClass = executor::class.findAnnotation<Documentation>()
            ?: error("${executor::class} does not have a Documentation annotation")
        return builder.toType(
            type = docClass.value.starProjectedType,
            description = getAPITypeDescription(executor::class)
        )
    }
}

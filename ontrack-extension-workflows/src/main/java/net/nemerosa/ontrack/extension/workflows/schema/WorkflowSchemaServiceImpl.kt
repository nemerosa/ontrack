package net.nemerosa.ontrack.extension.workflows.schema

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.json.schema.jsonSchema
import net.nemerosa.ontrack.model.support.EnvService
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class WorkflowSchemaServiceImpl(
    private val envService: EnvService,
    private val applicationContext: ApplicationContext,
) : WorkflowSchemaService {

    override fun createJsonSchema(): JsonNode {
        val schema = jsonSchema(
            ref = "workflow",
            id = "https://ontrack.run/${envService.version.display}/schema/workflow",
            title = "Ontrack workflow",
            description = "Workflow definition for Ontrack",
            root = Workflow::class,
        ) { cls ->
            applicationContext.getBeansOfType(cls.java).values.single()
        }
        return schema.asJson()
    }

}
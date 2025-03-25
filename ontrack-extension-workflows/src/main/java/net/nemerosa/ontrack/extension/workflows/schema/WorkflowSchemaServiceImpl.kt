package net.nemerosa.ontrack.extension.workflows.schema

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.json.schema.JsonSchemaBuilderService
import net.nemerosa.ontrack.model.support.EnvService
import org.springframework.stereotype.Service

@Service
class WorkflowSchemaServiceImpl(
    private val envService: EnvService,
    private val jsonSchemaBuilderService: JsonSchemaBuilderService,
) : WorkflowSchemaService {

    override fun createJsonSchema(): JsonNode =
        jsonSchemaBuilderService.createSchema(
            ref = "workflow",
            id = "https://ontrack.run/${envService.version.display}/schema/workflow",
            title = "Ontrack workflow",
            description = "Workflow definition for Ontrack",
            root = Workflow::class,
        ).asJson()

}
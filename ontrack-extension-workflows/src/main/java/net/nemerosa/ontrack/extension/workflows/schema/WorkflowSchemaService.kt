package net.nemerosa.ontrack.extension.workflows.schema

import com.fasterxml.jackson.databind.JsonNode

interface WorkflowSchemaService {

    /**
     * Creating the JSON Schema for a workflow
     */
    fun createJsonSchema(): JsonNode

}
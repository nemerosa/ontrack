package net.nemerosa.ontrack.extension.workflows.schema

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/extension/workflows")
class WorkflowSchemaController(
    private val workflowSchemaService: WorkflowSchemaService,
) {

    /**
     * Downloading the Workflows JSON schema
     */
    @GetMapping("download/schema/json")
    fun downloadJSONSchema(response: HttpServletResponse) {
        val json = workflowSchemaService.createJsonSchema()
            .toPrettyString()
            .toByteArray(Charsets.UTF_8)

        response.contentType = "application/json"
        response.setHeader("Content-Disposition", "attachment; filename=ontrack-workflow-schema.json")
        response.characterEncoding = "UTF-8"

        // Write JSON content to response output stream
        response.outputStream.write(json)
        response.outputStream.flush()
    }
}
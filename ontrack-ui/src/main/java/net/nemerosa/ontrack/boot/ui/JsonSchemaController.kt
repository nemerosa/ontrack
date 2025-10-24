package net.nemerosa.ontrack.boot.ui

import jakarta.servlet.http.HttpServletResponse
import net.nemerosa.ontrack.model.json.schema.JsonSchemaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Downloading JSON schemas based on their internal keys.
 */
@RestController
@RequestMapping("/rest/ref/schema/json")
class JsonSchemaController(
    private val jsonSchemaService: JsonSchemaService,
) {

    @GetMapping("/{key}")
    fun downloadJSONSchema(@PathVariable key: String, response: HttpServletResponse) {
        val json = jsonSchemaService.getJsonSchema(key)
            .toPrettyString()
            .toByteArray(Charsets.UTF_8)

        response.contentType = "application/json"
        response.setHeader("Content-Disposition", "attachment; filename=yontrack-$key-schema.json")
        response.characterEncoding = "UTF-8"

        // Write JSON content to response output stream
        response.outputStream.write(json)
        response.outputStream.flush()
    }

}
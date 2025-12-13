package net.nemerosa.ontrack.extension.casc.ui

import jakarta.servlet.http.HttpServletResponse
import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import net.nemerosa.ontrack.extension.casc.CascLoadingService
import net.nemerosa.ontrack.extension.casc.schema.json.CascJsonSchemaService
import net.nemerosa.ontrack.extension.casc.upload.CascUploadConstants
import net.nemerosa.ontrack.extension.casc.upload.CascUploadService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/extension/casc")
class CascController(
    private val cascLoadingService: CascLoadingService,
    private val cascConfigurationProperties: CascConfigurationProperties,
    private val cascUploadService: CascUploadService,
    private val cascJsonSchemaService: CascJsonSchemaService,
) {

    /**
     * Relods the configuration
     */
    @PutMapping("reload")
    fun reload() {
        cascLoadingService.load()
    }

    /**
     * Upload end point
     */
    @PostMapping("upload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun upload(@RequestParam file: MultipartFile) {
        if (cascConfigurationProperties.upload.enabled) {
            val type = file.contentType
            if (type != CascUploadConstants.TYPE) {
                throw CascUploadWrongTypeException(type)
            } else {
                val content = file.bytes.toString(Charsets.UTF_8)
                cascUploadService.upload(content)
                cascLoadingService.load()
            }
        } else {
            throw CascUploadNotEnabledException()
        }
    }

    /**
     * Downloading the Casc JSON schema
     */
    @GetMapping("download/schema/json")
    fun downloadJSONSchema(response: HttpServletResponse) {
        val json = cascJsonSchemaService.createJsonSchema()
            .toPrettyString()
            .toByteArray(Charsets.UTF_8)

        response.contentType = "application/json"
        response.setHeader("Content-Disposition", "attachment; filename=ontrack-casc-schema.json")
        response.characterEncoding = "UTF-8"

        // Write JSON content to response output stream
        response.outputStream.write(json)
        response.outputStream.flush()
    }

}
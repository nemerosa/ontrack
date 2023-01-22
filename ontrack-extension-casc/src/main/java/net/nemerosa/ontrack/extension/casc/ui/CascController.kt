package net.nemerosa.ontrack.extension.casc.ui

import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import net.nemerosa.ontrack.extension.casc.CascLoadingService
import net.nemerosa.ontrack.extension.casc.upload.CascUploadConstants
import net.nemerosa.ontrack.extension.casc.upload.CascUploadService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/extension/casc")
class CascConcd troller(
    private val cascLoadingService: CascLoadingService,
    private val cascConfigurationProperties: CascConfigurationProperties,
    private val cascUploadService: CascUploadService,
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
            }
        } else {
            throw CascUploadNotEnabledException()
        }
    }

}
package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.ui.support.UIUtils.setupDefaultImageCache
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/rest/extension/environments")
class EnvironmentController(
    private val environmentService: EnvironmentService,
) {

    @PutMapping("environments/{environmentId}/image")
    @ResponseStatus(HttpStatus.OK)
    fun putEnvironmentImage(
        @PathVariable environmentId: String,
        @RequestBody image: String,
    ) {
        environmentService.setEnvironmentImage(
            environmentId,
            Document(
                "image/png",
                Base64.getDecoder().decode(image)
            )
        )
    }

    @GetMapping("environments/{environmentId}/image")
    fun getEnvironmentImage(response: HttpServletResponse, @PathVariable environmentId: String): Document {
        val document: Document = environmentService.getEnvironmentImage(environmentId)
        setupDefaultImageCache(response, document)
        return document
    }
}
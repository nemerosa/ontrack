package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.extension.api.ExtensionManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

/**
 * Generic hook controller
 */
@RestController
class HookController(
    private val extensionManager: ExtensionManager,
) {

    private val logger: Logger = LoggerFactory.getLogger(HookController::class.java)

    /**
     * Unique entrypoint for the hooks.
     */
    @PostMapping("/hook/secured/{hook}")
    fun hook(
        @PathVariable hook: String,
        @RequestBody body: String,
        @RequestParam parameters: Map<String, String>,
        @RequestHeader headers: Map<String, String>,
    ): HookResponse {

        // Getting the extension
        val endpoint = extensionManager.getExtensions(HookEndpointExtension::class.java)
            .find { it.id == hook }
            ?: throw HookNotFoundException(hook)

        // Checking if the endpoint is enabled
        if (!endpoint.enabled) {
            return hookDisabled(hook)
        }

        // Request representation
        val request = HookRequest(body, parameters, headers)

        // Checking the access
        endpoint.checkAccess(request)

        // Processing
        return endpoint.process(request)
    }
}
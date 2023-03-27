package net.nemerosa.ontrack.extension.hook

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.hook.metrics.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

/**
 * Generic hook controller
 */
@RestController
class HookController(
    private val extensionManager: ExtensionManager,
    private val meterRegistry: MeterRegistry,
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
        if (endpoint == null) {
            meterRegistry.hookUndefined(hook)
            throw HookNotFoundException(hook)
        }

        // Checking if the endpoint is enabled
        if (!endpoint.enabled) {
            meterRegistry.hookDisabled(hook)
            return hookDisabled(hook)
        }

        // Request representation
        val request = HookRequest(body, parameters, headers)

        // Checking the access
        try {
            endpoint.checkAccess(request)
        } catch (any: Exception) {
            meterRegistry.hookAccessDenied(hook)
            throw any
        }

        // Processing
        return try {
            val result = endpoint.process(request)
            meterRegistry.hookSuccess(hook)
            result
        } catch (any: Exception) {
            meterRegistry.hookError(hook)
            logger.error("[$hook] Uncaught error while processing a hook", any)
            throw any
        }
    }
}
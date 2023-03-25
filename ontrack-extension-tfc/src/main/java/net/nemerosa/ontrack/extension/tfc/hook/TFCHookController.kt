package net.nemerosa.ontrack.extension.tfc.hook

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller called by the TFC notifications.
 */
@RestController
@RequestMapping("/hook/secured/tfc")
class TFCHookController {

    private val logger: Logger = LoggerFactory.getLogger(TFCHookController::class.java)

    @PostMapping("")
    fun hook(
        @RequestBody body: String,
        params: TFCHookParameters,
    ): TFCHookResponse {
        // Logging
        logger.info("Body: $body")
        logger.info("Params: $params")
        // TODO Response
        return TFCHookResponse("test ok")
    }
}
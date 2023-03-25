package net.nemerosa.ontrack.extension.tfc.hook

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.tfc.hook.dispatching.TFCHookDispatcher
import net.nemerosa.ontrack.extension.tfc.hook.model.TFCHookPayload
import net.nemerosa.ontrack.extension.tfc.metrics.TFCMetrics
import net.nemerosa.ontrack.extension.tfc.settings.TFCSettings
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.metrics.increment
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

/**
 * Controller called by the TFC notifications.
 */
@RestController
@RequestMapping("/hook/secured/tfc")
class TFCHookController(
    private val cachedSettingsService: CachedSettingsService,
    private val tfcHookSignatureService: TFCHookSignatureService,
    private val meterRegistry: MeterRegistry,
    private val tfcHookDispatcher: TFCHookDispatcher,
) {

    private val logger: Logger = LoggerFactory.getLogger(TFCHookController::class.java)

    @PostMapping("")
    fun hook(
        @RequestBody body: String,
        params: TFCHookParameters,
        @RequestHeader("X-TFE-Notification-Signature") signature: String,
    ): TFCHookResponse {
        // TODO
        logger.info("Signature = $signature")
        // Checking if the hook is enabled
        val settings = cachedSettingsService.getCachedSettings(TFCSettings::class.java)
        if (!settings.enabled) {
            throw TFCHookDisabledException()
        }

        // Checking the signature
        val json = when (tfcHookSignatureService.checkPayloadSignature(body, signature)) {

            TFCHookSignatureCheck.MISMATCH -> {
                meterRegistry.increment(TFCMetrics.Hook.signatureErrorCount)
                throw TFCHookSignatureMismatchException()
            }

            TFCHookSignatureCheck.MISSING_TOKEN -> throw TFCSettingsMissingTokenException()
            TFCHookSignatureCheck.OK -> body.parseAsJson()
        }
        // Parsing
        val payload = json.parseOrNull<TFCHookPayload>()
            ?: throw TFCHookPayloadParsingException()
        // Dispatching
        return tfcHookDispatcher.dispatch(params, payload)
    }
}
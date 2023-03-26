package net.nemerosa.ontrack.extension.tfc.hook

import net.nemerosa.ontrack.extension.hook.HookEndpointExtension
import net.nemerosa.ontrack.extension.hook.HookRequest
import net.nemerosa.ontrack.extension.hook.HookResponse
import net.nemerosa.ontrack.extension.hook.HookSignature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.tfc.TFCConfigProperties
import net.nemerosa.ontrack.extension.tfc.TFCExtensionFeature
import net.nemerosa.ontrack.extension.tfc.settings.TFCSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TFCHookEndpointExtension(
    private val cachedSettingsService: CachedSettingsService,
    private val tfcConfigProperties: TFCConfigProperties,
    extensionFeature: TFCExtensionFeature,
) : AbstractExtension(extensionFeature), HookEndpointExtension {

    private val logger: Logger = LoggerFactory.getLogger(TFCHookEndpointExtension::class.java)

    override val id: String = "tfc"

    override val enabled: Boolean
        get() = cachedSettingsService.getCachedSettings(TFCSettings::class.java).enabled

    override fun checkAccess(request: HookRequest) {
        if (tfcConfigProperties.hook.signature.disabled) {
            logger.warn("TFC Hook signature checks are disabled.")
        } else {
            val token = cachedSettingsService.getCachedSettings(TFCSettings::class.java).token
            HookSignature.checkSignature(
                request.body,
                request.getRequiredHeader("X-TFE-Notification-Signature"),
                token
            )
        }
    }

    override fun process(request: HookRequest): HookResponse {
        TODO("Not yet implemented")
    }
}
package net.nemerosa.ontrack.extension.tfc.hook

import net.nemerosa.ontrack.extension.tfc.TFCConfigProperties
import net.nemerosa.ontrack.extension.tfc.settings.TFCSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TFCHookSignatureServiceImpl(
    private val cachedSettingsService: CachedSettingsService,
    private val tfcConfigProperties: TFCConfigProperties,
) : TFCHookSignatureService {

    private val logger: Logger = LoggerFactory.getLogger(TFCHookSignatureServiceImpl::class.java)

    override fun checkPayloadSignature(body: String, signature: String): TFCHookSignatureCheck {
        if (tfcConfigProperties.hook.signature.disabled) {
            logger.warn("TFC Hook signature checks are disabled.")
            return TFCHookSignatureCheck.OK
        }
        val token = cachedSettingsService.getCachedSettings(TFCSettings::class.java).token
        if (token.isBlank()) {
            return TFCHookSignatureCheck.MISSING_TOKEN
        }
        return TFCHookSignature.checkPayloadSignature(body, signature, token)
    }

}
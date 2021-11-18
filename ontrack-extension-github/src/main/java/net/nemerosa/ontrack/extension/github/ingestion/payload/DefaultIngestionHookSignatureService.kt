package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.extension.github.ingestion.IngestionConfigProperties
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * See https://docs.github.com/en/developers/webhooks-and-events/webhooks/securing-your-webhooks for the implementation.
 */
@Component
class DefaultIngestionHookSignatureService(
    private val cachedSettingsService: CachedSettingsService,
    private val ingestionConfigProperties: IngestionConfigProperties,
) : IngestionHookSignatureService {

    private val logger: Logger = LoggerFactory.getLogger(DefaultIngestionHookSignatureService::class.java)

    override fun checkPayloadSignature(body: String, signature: String): IngestionHookSignatureCheckResult {
        if (ingestionConfigProperties.hook.signature.disabled) {
            logger.warn("GitHub Ingestion Hook signature checks are disabled.")
            return IngestionHookSignatureCheckResult.OK
        }
        val token = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java).token
        if (token.isBlank()) {
            return IngestionHookSignatureCheckResult.MISSING_TOKEN
        }
        return IngestionHookSignature.checkPayloadSignature(body, signature, token)
    }
}
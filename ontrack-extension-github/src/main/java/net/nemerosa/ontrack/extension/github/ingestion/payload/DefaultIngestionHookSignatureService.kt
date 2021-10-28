package net.nemerosa.ontrack.extension.github.ingestion.payload

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettingsMissingTokenException
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component

/**
 * See https://docs.github.com/en/developers/webhooks-and-events/webhooks/securing-your-webhooks for the implementation.
 */
@Component
class DefaultIngestionHookSignatureService(
    private val cachedSettingsService: CachedSettingsService,
) : IngestionHookSignatureService {

    override fun checkPayloadSignature(body: String, signature: String): JsonNode {
        val token = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java).token
        if (token.isBlank()) {
            throw GitHubIngestionSettingsMissingTokenException()
        }
        IngestionHookSignature.checkPayloadSignature(body, signature, token)
        return body.parseAsJson()
    }
}
package net.nemerosa.ontrack.extension.slack.service

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import net.nemerosa.ontrack.extension.slack.SlackSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultSlackService(
    private val cachedSettingsService: CachedSettingsService,
    private val applicationLogService: ApplicationLogService,
) : SlackService {

    override fun sendNotification(channel: String, message: String): Boolean {
        val settings = cachedSettingsService.getCachedSettings(SlackSettings::class.java)
        val iconEmoji = settings.emoji.takeIf { it.isNotBlank() }
        return if (settings.enabled) {
            // Gets the client
            val client = getSlackClient(settings.token, null)
            // Sending the message
            return try {
                val response = client.chatPostMessage {
                    it.channel(channel).text(message).mrkdwn(true).run {
                        if (iconEmoji.isNullOrBlank()) {
                            this
                        } else {
                            iconEmoji(iconEmoji)
                        }
                    }
                }
                if (response.isOk) {
                    true
                } else {
                    throw SlackServiceException(response.error?.takeIf { it.isNotBlank() }?.let { "Slack message could not be sent: $it" } ?: "Slack message could not be sent (no additional detail).")
                }
            } catch (ex: Exception) {
                // Logs the error
                applicationLogService.log(
                    ApplicationLogEntry.error(
                        ex,
                        NameDescription.nd("slack-error", "Slack notification error"),
                        "Cannot send Slack message: ${ex.message}"
                    ).withDetail("channel", channel)
                )
                // OK
                return false
            }
        } else {
            false
        }
    }

    fun getSlackClient(slackToken: String, endpointUrl: String? = null): MethodsClient {
        val methods = Slack.getInstance().methods(slackToken)
        if (endpointUrl != null && endpointUrl.isNotBlank()) {
            methods.endpointUrlPrefix = endpointUrl
        }
        return methods
    }
}
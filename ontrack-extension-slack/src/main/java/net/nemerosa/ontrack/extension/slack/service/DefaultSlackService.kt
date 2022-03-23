package net.nemerosa.ontrack.extension.slack.service

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import net.nemerosa.ontrack.extension.slack.SlackSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultSlackService(
    private val cachedSettingsService: CachedSettingsService,
) : SlackService {

    override fun sendNotification(channel: String, message: String, iconEmoji: String?): Boolean {
        val settings = cachedSettingsService.getCachedSettings(SlackSettings::class.java)
        return if (settings.enabled) {
            // Gets the client
            val client = getSlackClient(settings.token, null)
            // Sending the message
            val response = try {
                client.chatPostMessage {
                    it.channel(channel).text(message).run {
                        if (iconEmoji.isNullOrBlank()) {
                            this
                        } else {
                            iconEmoji(iconEmoji)
                        }
                    }
                }
            } catch (ex: Exception) {
                // TODO Logs the error
                // OK
                return false
            }
            // Result
            if (!response.isOk) {
                // TODO Logs the error
                // OK
                false
            } else {
                true
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
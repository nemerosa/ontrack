package net.nemerosa.ontrack.extension.slack.service

import com.slack.api.model.block.composition.BlockCompositions.markdownText
import net.nemerosa.ontrack.extension.slack.SlackSettings
import net.nemerosa.ontrack.extension.slack.client.SlackClient
import net.nemerosa.ontrack.extension.slack.client.SlackClientFactory
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultSlackService(
    private val cachedSettingsService: CachedSettingsService,
    private val slackClientFactory: SlackClientFactory,
) : SlackService {

    private val logger = LoggerFactory.getLogger(DefaultSlackService::class.java)

    override fun sendNotification(channel: String, message: String, type: SlackNotificationType?): Boolean {
        val settings = cachedSettingsService.getCachedSettings(SlackSettings::class.java)
        return if (settings.enabled) {
            // Gets the client
            val client = getSlackClient(settings.token, settings.endpoint)
            // Sending the message
            return try {
                val iconEmoji = settings.emoji?.takeIf { it.isNotBlank() }
                val color = type?.color
                val markdown = markdownText(message)
                val response = client.send(channel, markdown, iconEmoji, color)
//                val response = client.chatPostMessage { req ->
//                    req
//                        .channel(channel)
//                        .attachments(
//                            listOf(
//                                Attachment.builder()
//                                    .run {
//                                        val color = type?.color
//                                        if (color.isNullOrBlank()) {
//                                            this
//                                        } else {
//                                            color(color)
//                                        }
//                                    }
//                                    .blocks(
//                                        asBlocks(
//                                            section { section ->
//                                                section.text(markdownText(message))
//                                            }
//                                        )
//                                    )
//                                    .build()
//                            )
//                        )
//                        .run {
//                            if (iconEmoji.isNullOrBlank()) {
//                                this
//                            } else {
//                                iconEmoji(iconEmoji)
//                            }
//                        }
//                }
                if (response.ok) {
                    true
                } else {
                    throw SlackServiceException(response.error?.takeIf { it.isNotBlank() }
                        ?.let { "Slack message could not be sent: $it" }
                        ?: "Slack message could not be sent (no additional detail).")
                }
            } catch (ex: Exception) {
                // Logs the error
                logger.error(
                    "Cannot send Slack message on channel ${channel}: ${ex.message}",
                    ex
                )
                // OK
                return false
            }
        } else {
            false
        }
    }

    fun getSlackClient(slackToken: String, endpointUrl: String? = null): SlackClient =
        slackClientFactory.getSlackClient(slackToken, endpointUrl)
}
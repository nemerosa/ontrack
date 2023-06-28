package net.nemerosa.ontrack.extension.slack.service

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.model.Attachment
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.composition.BlockCompositions.markdownText
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

    override fun sendNotification(channel: String, message: String, type: SlackNotificationType?): Boolean {
        val settings = cachedSettingsService.getCachedSettings(SlackSettings::class.java)
        val iconEmoji = settings.emoji?.takeIf { it.isNotBlank() }
        return if (settings.enabled) {
            // Gets the client
            val client = getSlackClient(settings.token, settings.endpoint)
            // Sending the message
            return try {
                val response = client.chatPostMessage { req -> req
                    .channel(channel)
                    .attachments(
                        listOf(
                            Attachment.builder()
                                .run {
                                    val color = type?.color
                                    if (color.isNullOrBlank()) {
                                        this
                                    } else {
                                        color(color)
                                    }
                                }
                                .blocks(
                                    asBlocks(
                                        section { section ->
                                            section.text(markdownText(message))
                                        }
                                    )
                                )
                                .build()
                        )
                    )
                    .run {
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
        if (!endpointUrl.isNullOrBlank()) {
            methods.endpointUrlPrefix = endpointUrl
        }
        return methods
    }
}
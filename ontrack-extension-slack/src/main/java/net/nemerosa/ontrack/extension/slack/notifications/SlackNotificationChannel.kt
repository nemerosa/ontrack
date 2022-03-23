package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.slack.SlackSettings
import net.nemerosa.ontrack.extension.slack.service.SlackService
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component

@Component
class SlackNotificationChannel(
    private val slackService: SlackService,
    private val cachedSettingsService: CachedSettingsService,
    private val eventRenderer: EventRenderer,
) : AbstractNotificationChannel<SlackNotificationChannelConfig>(SlackNotificationChannelConfig::class) {

    override fun publish(config: SlackNotificationChannelConfig, event: Event): NotificationResult {
        // Formatting the message
        val message = format(event)
        // Sending the message
        // TODO Icon emoji from the settings
        val sent = slackService.sendNotification(config.channel, message, iconEmoji = null)
        // Result
        return if (sent) {
            NotificationResult.ok()
        } else {
            NotificationResult.error("Slack message could not be sent. Check the operational logs.")
        }
    }

    private fun format(event: Event): String = event.render(eventRenderer)

    override val type: String = "slack"

    override val enabled: Boolean
        get() =
            cachedSettingsService.getCachedSettings(SlackSettings::class.java).enabled
}
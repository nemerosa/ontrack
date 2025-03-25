package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.extension.slack.service.SlackNotificationType
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

data class SlackNotificationChannelConfig(
    @APILabel("Channel")
    @APIDescription("Slack channel")
    val channel: String,
    @APILabel("Notification type")
    @APIDescription("Used for the color of the message")
    val type: SlackNotificationType = SlackNotificationType.INFO,
)
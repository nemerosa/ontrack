package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.extension.slack.service.SlackNotificationType
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

data class SlackNotificationChannelConfig(
    @APIName("Channel")
    @APIDescription("Slack channel")
    val channel: String,
    @APIName("Notification type")
    @APIDescription("Used for the color of the message")
    val type: SlackNotificationType = SlackNotificationType.INFO,
)
package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.common.api.APIDescription

data class SlackNotificationChannelOutput(
    @APIDescription("Actual content of the message")
    val message: String,
)

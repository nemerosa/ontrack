package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

@APIDescription("Settings to connect Ontrack to a Slack instance")
class SlackSettings(
    @APILabel("Enabled")
    @APIDescription("Is Slack communication enabled?")
    val enabled: Boolean = false,
    @APILabel("Token")
    @APIDescription("Slack token")
    val token: String = "",
    @APILabel("Emoji")
    @APIDescription("Emoji (like :ontrack:) to use for the message")
    val emoji: String? = "",
    @APILabel("Endpoint")
    @APIDescription("Slack API endpoint (leave blank for default)")
    val endpoint: String? = "",
)

package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Settings to connect Ontrack to a Slack instance")
class SlackSettings(
    @APIDescription("Is Slack communication enabled?")
    val enabled: Boolean = false,
    @APIDescription("Slack token")
    val token: String = "",
)

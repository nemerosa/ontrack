package net.nemerosa.ontrack.extension.slack.client

data class MockSlackMessage(
    val channel: String,
    val color: String?,
    val iconEmoji: String?,
    val markdown: String,
)

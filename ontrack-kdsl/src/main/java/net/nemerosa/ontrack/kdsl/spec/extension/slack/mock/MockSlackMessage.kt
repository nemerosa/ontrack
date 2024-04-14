package net.nemerosa.ontrack.kdsl.spec.extension.slack.mock

data class MockSlackMessage(
    val channel: String,
    val color: String?,
    val iconEmoji: String?,
    val markdown: String,
)

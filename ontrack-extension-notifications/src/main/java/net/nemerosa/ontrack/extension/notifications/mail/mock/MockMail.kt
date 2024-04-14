package net.nemerosa.ontrack.extension.notifications.mail.mock

data class MockMail(
    val to: String,
    val cc: String?,
    val subject: String,
    val body: String?,
)

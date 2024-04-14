package net.nemerosa.ontrack.kdsl.spec.extension.notifications.mail

data class MockMail(
    val to: String,
    val cc: String?,
    val subject: String,
    val body: String?,
)

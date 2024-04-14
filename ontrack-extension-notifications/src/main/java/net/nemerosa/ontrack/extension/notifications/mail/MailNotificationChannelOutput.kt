package net.nemerosa.ontrack.extension.notifications.mail

data class MailNotificationChannelOutput(
    val to: String,
    val cc: String? = null,
    val subject: String,
    val body: String,
)

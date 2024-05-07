package net.nemerosa.ontrack.extension.notifications.mail

import net.nemerosa.ontrack.model.annotations.APIDescription

data class MailNotificationChannelOutput(
    @APIDescription("List of recipients")
    val to: String,
    @APIDescription("List of recipients in cc")
    val cc: String? = null,
    @APIDescription("Actual generated subject for the mail")
    val subject: String,
    @APIDescription("Actual generated body for the mail")
    val body: String,
)

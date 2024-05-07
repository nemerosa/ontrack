package net.nemerosa.ontrack.extension.notifications.mail

import net.nemerosa.ontrack.model.annotations.APIDescription

data class MailNotificationChannelConfig(
    @APIDescription("Comma-separated list of mail targets (to)")
    val to: String,
    @APIDescription("Comma-separated list of mail targets (cc)")
    val cc: String?,
    @APIDescription("(template) Mail subject")
    val subject: String,
)
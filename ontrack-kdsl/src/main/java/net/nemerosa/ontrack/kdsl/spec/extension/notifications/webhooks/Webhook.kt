package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

data class Webhook(
    val name: String,
    val enabled: Boolean?,
    val url: String?,
    val timeoutSeconds: Int?,
    val authenticationType: String?,
)
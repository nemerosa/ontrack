package net.nemerosa.ontrack.extension.environments.notifications

data class SlotPipelineCreationNotificationChannelConfig(
    val environment: String,
    val qualifier: String? = null,
)

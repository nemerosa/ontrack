package net.nemerosa.ontrack.extension.notifications.inmemory

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

data class InMemoryNotificationChannelConfig(
    @APILabel("Group")
    @APIDescription("Group of messages")
    val group: String,
)
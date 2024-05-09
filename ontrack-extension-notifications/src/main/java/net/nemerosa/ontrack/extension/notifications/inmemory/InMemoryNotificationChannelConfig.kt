package net.nemerosa.ontrack.extension.notifications.inmemory

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

data class InMemoryNotificationChannelConfig(
    @APILabel("Group")
    @APIDescription("Group of messages")
    val group: String,
    @APIDescription("Optional data to pass along to the output")
    val data: String? = null,
)
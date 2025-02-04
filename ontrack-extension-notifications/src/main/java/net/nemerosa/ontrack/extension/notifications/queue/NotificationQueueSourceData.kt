package net.nemerosa.ontrack.extension.notifications.queue

import net.nemerosa.ontrack.model.structure.ProjectEntityID

data class NotificationQueueSourceData(
    val projectEntityID: ProjectEntityID?,
    val subscriptionName: String,
)

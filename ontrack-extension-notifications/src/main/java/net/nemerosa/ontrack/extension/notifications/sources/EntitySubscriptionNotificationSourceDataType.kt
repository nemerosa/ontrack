package net.nemerosa.ontrack.extension.notifications.sources

import net.nemerosa.ontrack.model.structure.ProjectEntityType

data class EntitySubscriptionNotificationSourceDataType(
    val entityType: ProjectEntityType,
    val entityId: Int,
    val subscriptionName: String,
)

package net.nemerosa.ontrack.extension.notifications.sources

import net.nemerosa.ontrack.model.structure.ProjectEntityType

data class EntitySubscriptionNotificationSourceDataType(
    private val entityType: ProjectEntityType,
    private val entityId: Int,
    private val subscriptionName: String,
)

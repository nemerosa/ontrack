package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.exceptions.NotFoundException
import net.nemerosa.ontrack.model.structure.ProjectEntity

class EventSubscriptionIdNotFoundException(projectEntity: ProjectEntity?, id: String) : NotFoundException(
    if (projectEntity != null) {
        "The ${projectEntity.entityDisplayName} subscription with ID $id cannot be found."
    } else {
        "The global subscription with ID $id cannot be found."
    }
)

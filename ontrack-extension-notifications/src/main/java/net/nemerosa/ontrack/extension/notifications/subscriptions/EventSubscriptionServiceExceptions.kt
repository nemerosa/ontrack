package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.exceptions.NotFoundException
import net.nemerosa.ontrack.model.structure.ProjectEntity

class EventSubscriptionNameNotFoundException(projectEntity: ProjectEntity?, name: String) : NotFoundException(
    if (projectEntity != null) {
        "The ${projectEntity.entityDisplayName} subscription with name $name cannot be found."
    } else {
        "The global subscription with name $name cannot be found."
    }
)

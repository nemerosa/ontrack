package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.exceptions.NotFoundException
import net.nemerosa.ontrack.model.structure.ProjectEntity

class EventSubscriptionIdNotFoundException(projectEntity: ProjectEntity?, id: String) : NotFoundException(
    if (projectEntity != null) {
        "The ${projectEntity.entityDisplayName} subscription with ID $id cannot be found."
    } else {
        "The global subscription with ID $id cannot be found."
    }
)

class EventSubscriptionFilterEntityRequired : InputException(
    """When using scope = ENTITY, the entity is required in the filter."""
)
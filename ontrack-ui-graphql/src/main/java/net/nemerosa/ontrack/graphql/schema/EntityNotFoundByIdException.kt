package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.model.exceptions.NotFoundException
import net.nemerosa.ontrack.model.structure.ProjectEntityType

class EntityNotFoundByIdException(
    type: ProjectEntityType,
    id: Int,
) : NotFoundException(
    """Cannot find ${type.displayName} with ID $id."""
)
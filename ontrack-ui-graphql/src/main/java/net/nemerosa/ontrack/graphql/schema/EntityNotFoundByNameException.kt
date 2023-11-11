package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.model.exceptions.NotFoundException
import net.nemerosa.ontrack.model.structure.ProjectEntityType

class EntityNotFoundByNameException(
    type: ProjectEntityType,
    names: Map<String, String>
) : NotFoundException(
    """Cannot find ${type.displayName} using names: $names."""
)
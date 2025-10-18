package net.nemerosa.ontrack.extension.config.extensions

import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.structure.ProjectEntityType

class CIConfigExtensionTypeNotSupportedException(projectEntityType: ProjectEntityType, id: String) : InputException(
    """CI config extension not supported for $projectEntityType: $id."""
)

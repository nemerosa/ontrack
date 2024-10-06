package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.structure.Project

class SlotAlreadyDefinedException(
    environment: Environment,
    project: Project,
    qualifier: String,
) : InputException(
    """
    The environment ${environment.name} has already a slot defined for project ${project.name} with qualifier "$qualifier". 
"""
)

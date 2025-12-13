package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class BuildReleaseNotFoundException(
    project: Project,
    name: String
) : NotFoundException(
    """Could not find build with release "$name" in ${project.name}."""
)
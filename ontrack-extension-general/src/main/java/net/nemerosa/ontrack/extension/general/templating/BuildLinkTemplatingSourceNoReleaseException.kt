package net.nemerosa.ontrack.extension.general.templating

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.templating.TemplatingException

class BuildLinkTemplatingSourceNoReleaseException(
    val build: Build,
) : TemplatingException(
    """${build.entityDisplayName} has not release property."""
)

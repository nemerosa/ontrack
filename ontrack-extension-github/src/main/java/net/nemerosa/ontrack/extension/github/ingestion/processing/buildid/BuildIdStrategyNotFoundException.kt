package net.nemerosa.ontrack.extension.github.ingestion.processing.buildid

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class BuildIdStrategyNotFoundException(id: String) : NotFoundException(
    """Build ID strategy [$id] was not found."""
)
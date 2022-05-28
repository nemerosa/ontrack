package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class TaggingStrategyNotFoundException(type: String) : NotFoundException(
    """Tagging strategy with name "$type" cannot be found."""
)
package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.model.exceptions.NotFoundException

fun <C> TaggingStrategyRegistry.getTaggingRegistry(type: String) =
    findTaggingStrategy<C>(type)
        ?: throw TaggingStrategyNotFoundException(type)

class TaggingStrategyNotFoundException(type: String) : NotFoundException(
    """Tagging strategy with name "$type" cannot be found."""
)

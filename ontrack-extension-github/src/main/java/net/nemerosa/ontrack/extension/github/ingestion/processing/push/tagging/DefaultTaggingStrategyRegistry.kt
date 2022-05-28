package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import org.springframework.stereotype.Component

@Component
class DefaultTaggingStrategyRegistry(
    strategies: List<TaggingStrategy<*>>,
) : TaggingStrategyRegistry {

    private val index = strategies.associateBy { it.type }

    @Suppress("UNCHECKED_CAST")
    override fun <C> findTaggingStrategy(type: String): TaggingStrategy<C>? =
        index[type] as TaggingStrategy<C>?

}
package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

/**
 * Index of tagging strategies.
 */
interface TaggingStrategyRegistry {

    fun <C> findTaggingStrategy(type: String): TaggingStrategy<C>?

}
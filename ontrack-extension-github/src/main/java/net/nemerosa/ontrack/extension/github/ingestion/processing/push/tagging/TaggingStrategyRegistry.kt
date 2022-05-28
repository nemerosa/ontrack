package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionTaggingConfig

/**
 * Index of tagging strategies.
 */
interface TaggingStrategyRegistry {

    fun getTaggingStrategies(tagging: IngestionTaggingConfig): List<ConfiguredTaggingStrategy<*>>

}
package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.extension.github.ingestion.config.model.tagging.IngestionTaggingConfig

/**
 * Index of tagging strategies.
 */
interface TaggingStrategyRegistry {

    fun getTaggingStrategies(tagging: IngestionTaggingConfig): List<ConfiguredTaggingStrategy<*>>

}
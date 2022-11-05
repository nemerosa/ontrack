package net.nemerosa.ontrack.extension.github.ingestion.config.model.tagging

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

@APIDescription("Configuration for the tagging processing.")
data class IngestionTaggingConfig(
    @APIName("commitProperty")
    @APIDescription("If the commit property strategy must be applied. True by default.")
    @get:JsonProperty("commit-property")
    val commitProperty: Boolean = true,
    @APIDescription("List of tagging strategies to apply")
    val strategies: List<IngestionTaggingStrategyConfig> = emptyList(),
)
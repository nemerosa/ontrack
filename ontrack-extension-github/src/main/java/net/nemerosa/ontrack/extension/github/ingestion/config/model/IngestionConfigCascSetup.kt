package net.nemerosa.ontrack.extension.github.ingestion.config.model

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * CasC for a given entity.
 */
data class IngestionConfigCascSetup(
    @APIDescription("Regular expression for the branches which can setup the entity")
    val includes: String = "main",
    @APIDescription("Regular expression to exclude branches")
    val excludes: String = "",
    @APIDescription("Casc configuration for the entity")
    val casc: JsonNode = NullNode.instance,
)
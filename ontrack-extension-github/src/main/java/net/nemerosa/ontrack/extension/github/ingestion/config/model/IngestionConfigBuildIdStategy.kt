package net.nemerosa.ontrack.extension.github.ingestion.config.model

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Build identification strategy")
data class IngestionConfigBuildIdStategy(
    @APIDescription("ID of the build identification strategy (null to use the default commit-based strategy)")
    val id: String? = null,
    @APIDescription("Configuration of the build identification strategy")
    val config: JsonNode = NullNode.instance,
)
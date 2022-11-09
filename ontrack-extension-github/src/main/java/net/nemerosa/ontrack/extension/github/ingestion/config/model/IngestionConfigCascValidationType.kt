package net.nemerosa.ontrack.extension.github.ingestion.config.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

data class IngestionConfigCascValidationType(
    @APIDescription("FQCN or shortcut for the data type")
    val type: String,
    @APIDescription("Data type configuration")
    val config: JsonNode? = null,
)
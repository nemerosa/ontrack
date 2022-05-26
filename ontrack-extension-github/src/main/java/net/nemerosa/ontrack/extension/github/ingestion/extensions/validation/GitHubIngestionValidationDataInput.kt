package net.nemerosa.ontrack.extension.github.ingestion.extensions.validation

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

/**
 * Validation data input
 */
@APIName("GitHubIngestionValidationDataInput")
@APIDescription("Validation data input")
data class GitHubIngestionValidationDataInput(
    @APIDescription("FQCN of the validation data type")
    val type: String,
    @APIDescription("Validation data")
    val data: JsonNode,
)
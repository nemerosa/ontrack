package net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion

import com.fasterxml.jackson.databind.JsonNode

data class GitHubIngestionValidationDataInput(
    val type: String,
    val data: JsonNode,
)
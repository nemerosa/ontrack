package net.nemerosa.ontrack.kdsl.spec

import com.fasterxml.jackson.databind.JsonNode

/**
 * Data for a validation run
 *
 * @property type FQCN of the data type
 * @property data Data as JSON
 */
data class ValidationRunData(
    val type: String,
    val data: JsonNode,
)
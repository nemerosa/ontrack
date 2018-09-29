package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.databind.JsonNode

/**
 * Association between a configuration service ID and an actual configuration data.
 */
open class ServiceConfiguration(
        val id: String,
        val data: JsonNode?
) {
    companion object {
        @JvmStatic
        fun of(node: JsonNode): ServiceConfiguration {
            return ServiceConfiguration(
                    node.get("id").asText(),
                    node.get("data")
            )
        }
    }
}

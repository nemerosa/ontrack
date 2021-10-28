package net.nemerosa.ontrack.extension.github.ingestion

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson

object IngestionHookFixtures {

    /**
     * Sample payload
     */
    val payload: JsonNode by lazy {
        mapOf(
            "test" to "value"
        ).asJson()
    }

}
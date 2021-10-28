package net.nemerosa.ontrack.extension.github.ingestion

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson
import java.util.*

object IngestionHookFixtures {

    /**
     * Sample payload
     */
    fun payload(): JsonNode {
        return mapOf(
            "id" to UUID.randomUUID().toString(),
            "test" to "value"
        ).asJson()
    }

}
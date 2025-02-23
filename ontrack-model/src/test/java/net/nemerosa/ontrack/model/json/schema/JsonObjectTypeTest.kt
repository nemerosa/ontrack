package net.nemerosa.ontrack.model.json.schema

import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JsonObjectTypeTest {

    @Test
    fun `Json output of a oneOf condition`() {
        val oneOf = JsonOneOf(
            conditions = listOf(
                JsonCondition(
                    constProperty = JsonConstProperty("executorId", "mock"),
                    refProperty = JsonRefProperty("data", "workflow-node-executor-mock"),
                ),
                JsonCondition(
                    constProperty = JsonConstProperty("executorId", "pause"),
                    refProperty = JsonRefProperty("data", "workflow-node-executor-pause"),
                )
            ),
        )
        assertEquals(
            listOf(
                mapOf(
                    "properties" to mapOf(
                        "executorId" to mapOf("const" to "mock"),
                        "data" to mapOf("\$ref" to "#/\$defs/workflow-node-executor-mock")
                    )
                ),
                mapOf(
                    "properties" to mapOf(
                        "executorId" to mapOf("const" to "pause"),
                        "data" to mapOf("\$ref" to "#/\$defs/workflow-node-executor-pause")
                    )
                ),
            ).asJson(),
            oneOf.asJson()
        )
    }

}
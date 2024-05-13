package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorkflowContextTest {

    @Test
    fun `Json representation`() {
        val context = WorkflowContext(
            key = "mock",
            value = mapOf("text" to "Some text").asJson()
        )
        val json = context.asJson()
        assertEquals(
            mapOf(
                "data" to listOf(
                    mapOf(
                        "key" to "mock",
                        "value" to mapOf(
                            "text" to "Some text"
                        )
                    )
                )
            ).asJson(),
            json
        )
    }

}
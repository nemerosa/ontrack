package net.nemerosa.ontrack.extension.workflows.definition

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorkflowNodeTest {

    @Test
    fun `Parsing without the description`() {
        assertEquals(
            WorkflowNode(
                id = "test",
                description = null,
                executorId = "mock",
                data = mapOf("text" to "Test").asJson(),
                parents = emptyList(),
                timeout = 300L,
            ),
            mapOf(
                "id" to "test",
                "executorId" to "mock",
                "data" to mapOf("text" to "Test"),
            ).asJson().parse()
        )
    }

}
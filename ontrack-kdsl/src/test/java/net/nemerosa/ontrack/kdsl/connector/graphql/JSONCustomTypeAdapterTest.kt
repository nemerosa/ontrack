package net.nemerosa.ontrack.kdsl.connector.graphql

import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class JSONCustomTypeAdapterTest {

    private val adapter = JSONCustomTypeAdapter()

    @Test
    fun `Input encoding`() {
        val input = mapOf(
            "configuration" to "gh05513124",
            "repository" to "owner/77bd738eac45",
            "indexationInterval" to 0,
            "issueServiceConfigurationIdentifier" to null
        ).asJson()
        val value = adapter.encode(input)
        val decoded = adapter.decode(value)
        assertEquals(
            input,
            decoded
        )
    }

}
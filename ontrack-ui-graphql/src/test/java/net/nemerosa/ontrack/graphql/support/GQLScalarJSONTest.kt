package net.nemerosa.ontrack.graphql.support

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import graphql.language.*
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GQLScalarJSONTest {

    @Test
    fun `Parse literal object`() {
        val input = ObjectValue(
            listOf(
                ObjectField("value", IntValue(30.toBigInteger()))
            )
        )
        val value = GQLScalarJSON.INSTANCE.coercing.parseLiteral(input)
        assertIs<ObjectNode>(value) {
            assertEquals(30, it.path("value").asInt())
        }
    }

    @Test
    fun `Parse literal array`() {
        val input = ArrayValue(
            listOf(
                StringValue("one"),
                StringValue("two")
            )
        )
        val value = GQLScalarJSON.INSTANCE.coercing.parseLiteral(input)
        assertIs<ArrayNode>(value) {
            assertEquals(
                listOf("one", "two"),
                it.map { it.asText() }
            )
        }
    }

    @Test
    fun `JSON text`() {
        // Data as string
        val data = "Some text"
        // Serialization
        val json = GQLScalarJSON.INSTANCE.coercing.serialize(data) as JsonNode
        // Checks the data
        assertEquals("Some text", json.asText())
    }

    @Test
    fun `Obfuscation of password`() {
        // Data with password
        val data = SampleConfig("user", "secret")
        // As JSON scalar
        val json = GQLScalarJSON.INSTANCE.coercing.serialize(data) as JsonNode
        // Checks the data
        assertEquals("user", json["user"].asText())
        assertTrue(json["password"].isNull, "Password field set to null")
    }

    @Test
    fun `Deep obfuscation of password in objects`() {
        // Data with password
        val data = SampleData("value", SampleConfig("user", "secret"))
        // As JSON scaler
        val json = GQLScalarJSON.INSTANCE.coercing.serialize(data) as JsonNode
        // Checks the data
        assertEquals("value", json["value"].asText())
        assertEquals("user", json["config"]["user"].asText())
        assertTrue(json["config"]["password"].isNull, "Password field set to null")
    }

    @Test
    fun `Deep obfuscation of password in arrays`() {
        // Data with password
        val data = SampleArray(
            "value",
            listOf(
                SampleConfig("user", "secret")
            )
        )
        // As JSON scaler
        val json = GQLScalarJSON.INSTANCE.coercing.serialize(data) as JsonNode
        // Checks the data
        assertEquals("value", json["value"].asText())
        assertEquals("user", json["configs"][0]["user"].asText())
        assertTrue(json["configs"][0]["password"].isNull, "Password set to null")
    }

    data class SampleConfig(
        val user: String,
        val password: String
    )

    data class SampleData(
        val value: String,
        val config: SampleConfig
    )

    data class SampleArray(
        val value: String,
        val configs: List<SampleConfig>
    )

}
package net.nemerosa.ontrack.model.json.schema

import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JsonSchemaTest {

    @Test
    fun `Schema as JSON`() {
        val schema = JsonSchema(
            ref = "workflow",
            id = "my-id",
            title = "my-title",
            description = "my-description",
            defs = emptyMap(),
            root = JsonObjectType(
                title = "Some object",
                description = "Some description",
                properties = mapOf(
                    "name" to JsonStringType("Some name")
                ),
                required = listOf("name"),
                additionalProperties = false,
            )
        )
        assertEquals(
            mapOf(
                "${'$'}schema" to JsonSchema.SCHEMA,
                "${'$'}id" to "my-id",
                "title" to "my-title",
                "description" to "my-description",
                "${'$'}defs" to mapOf(
                    "workflow" to mapOf(
                        "type" to "object",
                        "title" to "Some object",
                        "description" to "Some description",
                        "properties" to mapOf(
                            "name" to mapOf(
                                "type" to "string",
                                "description" to "Some name"
                            )
                        ),
                        "required" to listOf("name"),
                        "additionalProperties" to false
                    )
                ),
                "${'$'}ref" to "#/${'$'}defs/workflow"
            ).asJson(),
            schema.asJson()
        )
    }

}
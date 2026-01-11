package net.nemerosa.ontrack.model.docs

import com.fasterxml.jackson.annotation.JsonAlias
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DocumentationUtilsTest {

    data class JsonAliasTest(
        @JsonAlias("path")
        val targetPath: String
    )

    @Test
    fun `Retrieving fields for documentation with JsonAlias`() {
        val fields = getFieldsForDocumentationClass(JsonAliasTest::class)
        val field = fields.find { it.name == "targetPath" }
        assertNotNull(field) { "Field must be found" }
        assertEquals(listOf("path"), field.aliases)
    }
}

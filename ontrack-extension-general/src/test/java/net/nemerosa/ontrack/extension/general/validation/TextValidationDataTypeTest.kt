package net.nemerosa.ontrack.extension.general.validation

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import kotlin.test.assertEquals

class TextValidationDataTypeTest {

    private val dataType = TextValidationDataType(GeneralExtensionFeature())

    @Test
    fun toJson() {
        val json = dataType.toJson("Some text")
        assertIs<TextNode>(json) {
            assertEquals("Some text", it.asText())
        }
    }

    @Test
    fun fromJson() {
        val data = dataType.fromJson(JsonUtils.parseAsNode("\"Some text\""))
        assertEquals("Some text", data)
    }

}
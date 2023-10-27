package net.nemerosa.ontrack.extension.general

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MetaInfoPropertyItemTest {

    @Test
    fun `Parsing into item`() {
        // Name only
        assertEquals(
            MetaInfoPropertyItem(name = "name", value = null, category = null, link = null),
            MetaInfoPropertyItem.parse("name")
        )
        // Name with blank category
        assertEquals(
            MetaInfoPropertyItem(name = "name", value = null, category = "", link = null),
            MetaInfoPropertyItem.parse("/name")
        )
        // Name with category
        assertEquals(
            MetaInfoPropertyItem(name = "name", value = null, category = "category", link = null),
            MetaInfoPropertyItem.parse("category/name")
        )
        // Name and value
        assertEquals(
            MetaInfoPropertyItem(name = "name", value = "value", category = null, link = null),
            MetaInfoPropertyItem.parse("name:value")
        )
        // Name with blank category and value
        assertEquals(
            MetaInfoPropertyItem(name = "name", value = "value", category = "", link = null),
            MetaInfoPropertyItem.parse("/name:value")
        )
        // Name with category and value
        assertEquals(
            MetaInfoPropertyItem(name = "name", value = "value", category = "category", link = null),
            MetaInfoPropertyItem.parse("category/name:value")
        )
    }

    @Test
    fun `Search token`() {
        assertEquals(
            "/name:",
            MetaInfoPropertyItem(name = "name", value = null, category = null, link = null).toSearchToken()
        )
        assertEquals(
            "/name:value",
            MetaInfoPropertyItem(name = "name", value = "value", category = null, link = null).toSearchToken()
        )
        assertEquals(
            "category/name:",
            MetaInfoPropertyItem(name = "name", value = null, category = "category", link = null).toSearchToken()
        )
        assertEquals(
            "category/name:value",
            MetaInfoPropertyItem(name = "name", value = "value", category = "category", link = null).toSearchToken()
        )
    }

    @Test
    fun match_name_nok() {
        assertFalse(
                MetaInfoPropertyItem.of("name", "value").matchNameValue("nam", "value")
        )
    }

    @Test
    fun match_value_nok_with_exact_value() {
        assertFalse(
                MetaInfoPropertyItem.of("name", "value").matchNameValue("name", "val")
        )
    }

    @Test
    fun match_value_ok_with_exact_value() {
        assertTrue(
                MetaInfoPropertyItem.of("name", "value").matchNameValue("name", "value")
        )
    }

    @Test
    fun match_value_ok_with_pattern() {
        assertTrue(
                MetaInfoPropertyItem.of("name", "value").matchNameValue("name", "val*")
        )
    }

    @Test
    fun match_value_ok_with_wildcard() {
        assertTrue(
                MetaInfoPropertyItem.of("name", "value").matchNameValue("name", "*")
        )
    }

    @Test
    fun match_value_ok_with_blank() {
        assertTrue(
                MetaInfoPropertyItem.of("name", "value").matchNameValue("name", "")
        )
    }

}

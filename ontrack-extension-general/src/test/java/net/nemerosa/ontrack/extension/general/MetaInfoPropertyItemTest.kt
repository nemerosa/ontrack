package net.nemerosa.ontrack.extension.general

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MetaInfoPropertyItemTest {

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

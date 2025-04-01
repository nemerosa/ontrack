package net.nemerosa.ontrack.extension.indicators.computing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConfigurableIndicatorAttributeTypeTest {

    @Test
    fun `Required mapping from null`() {
        assertEquals("SHOULD", ConfigurableIndicatorAttributeType.REQUIRED.map(null))
    }

    @Test
    fun `Required mapping from empty string`() {
        assertEquals("SHOULD", ConfigurableIndicatorAttributeType.REQUIRED.map(""))
    }

    @Test
    fun `Required mapping from any string`() {
        assertEquals("SHOULD", ConfigurableIndicatorAttributeType.REQUIRED.map("null"))
    }

    @Test
    fun `Required mapping from false string`() {
        assertEquals("SHOULD", ConfigurableIndicatorAttributeType.REQUIRED.map("false"))
    }

    @Test
    fun `Required mapping from true string`() {
        assertEquals("MUST", ConfigurableIndicatorAttributeType.REQUIRED.map("true"))
    }

}
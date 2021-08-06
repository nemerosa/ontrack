package net.nemerosa.ontrack.json

import org.junit.Test
import kotlin.test.assertEquals

class KTJsonUtilsTest {

    @Test
    fun `getTextField with existing value`() {
        assertEquals("12", mapOf("value" to "12").asJson().getTextField("value"))
    }

    @Test
    fun `getTextField with null value`() {
        assertEquals(null, mapOf("value" to null).asJson().getTextField("value"))
    }

    @Test
    fun `getTextField with no value`() {
        assertEquals(null, mapOf("other" to "12").asJson().getTextField("value"))
    }

}
package net.nemerosa.ontrack.json

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JsonTransformTest {

    @Test
    fun `Text node`() {
        assertEquals(
            "TEST".asJson(),
            "test".asJson().transform { it.uppercase() }
        )
    }

    @Test
    fun `Boolean node`() {
        assertEquals(
            true.asJson(),
            true.asJson().transform { it.uppercase() }
        )
    }

    @Test
    fun `Int node`() {
        assertEquals(
            10.asJson(),
            10.asJson().transform { it.uppercase() }
        )
    }

    @Test
    fun `Array node`() {
        assertEquals(
            listOf("TEST", "ONE").asJson(),
            listOf("test", "one").asJson().transform { it.uppercase() }
        )
    }

    @Test
    fun `Object node`() {
        assertEquals(
            mapOf("name" to "TEST").asJson(),
            mapOf("name" to "Test").asJson().transform { it.uppercase() }
        )
    }

    @Test
    fun `Deep object node`() {
        assertEquals(
            mapOf(
                "person" to mapOf("name" to "TEST")
            ).asJson(),
            mapOf(
                "person" to mapOf("name" to "Test")
            ).asJson().transform { it.uppercase() }
        )
    }

    @Test
    fun `Deep object node in array`() {
        assertEquals(
            listOf(
                mapOf(
                    "person" to mapOf("name" to "TEST")
                )
            ).asJson(),
            listOf(
                mapOf(
                    "person" to mapOf("name" to "Test")
                )
            ).asJson().transform { it.uppercase() }
        )
    }

}
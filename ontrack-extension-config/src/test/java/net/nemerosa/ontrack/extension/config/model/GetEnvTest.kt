package net.nemerosa.ontrack.extension.config.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetEnvTest {

    @Test
    fun `First existing variable`() {
        val env = mapOf("A" to "value-a")
        assertEquals("value-a", env.getEnv("A", "B"))
    }

    @Test
    fun `Second existing variable if first one is missing`() {
        val env = mapOf("B" to "value-b")
        assertEquals("value-b", env.getEnv("A", "B"))
    }

    @Test
    fun `Second existing variable if first one is blank`() {
        val env = mapOf("A" to "  ", "B" to "value-b")
        assertEquals("value-b", env.getEnv("A", "B"))
    }

    @Test
    fun `Null if no variable exists`() {
        val env = mapOf("C" to "value-c")
        assertNull(env.getEnv("A", "B"))
    }

    @Test
    fun `Null if all variables are blank`() {
        val env = mapOf("A" to " ", "B" to "")
        assertNull(env.getEnv("A", "B"))
    }

    @Test
    fun `Null if no name provided`() {
        val env = mapOf("A" to "value-a")
        assertNull(env.getEnv())
    }

}
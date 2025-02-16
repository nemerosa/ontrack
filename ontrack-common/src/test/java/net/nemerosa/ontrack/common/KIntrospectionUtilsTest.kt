package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KIntrospectionUtilsTest {

    @Test
    fun `Has default value`() {
        data class Example(
            val name: String,
            val parents: List<String> = emptyList(),
        )

        assertFalse(Example::name.hasDefaultValue(Example::class))
        assertTrue(Example::parents.hasDefaultValue(Example::class))
    }

}
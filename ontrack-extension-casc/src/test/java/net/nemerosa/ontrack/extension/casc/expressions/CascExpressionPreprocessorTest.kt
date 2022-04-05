package net.nemerosa.ontrack.extension.casc.expressions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class CascExpressionPreprocessorTest {

    private val mockExpressionContext = object : CascExpressionContext {

        override val name: String = "mock"

        override fun evaluate(value: String): String = value.uppercase()
    }

    private val expressionPreprocessor = CascExpressionPreprocessor(
        listOf(
            mockExpressionContext
        )
    )

    @Test
    fun `No change when no pattern`() {
        assertEquals(
            "value: no-pattern",
            expressionPreprocessor.process("value: no-pattern")
        )
    }

    @Test
    fun `Error when no name`() {
        assertFailsWith<CascExpressionMissingNameException> {
            expressionPreprocessor.process("value: {{  }}")
        }
    }

    @Test
    fun `Error when no context`() {
        assertFailsWith<CascExpressionUnknownException> {
            expressionPreprocessor.process("value: {{ no-match }}")
        }
    }

    @Test
    fun `Matching a context`() {
        assertEquals(
            "value: TEST",
            expressionPreprocessor.process("value: {{ mock.test }}")
        )
    }

}
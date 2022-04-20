package net.nemerosa.ontrack.extension.casc.expressions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class CascExpressionPreprocessorTest {

    private val mockExpressionContext = object : CascExpressionContext {

        override val name: String = "mock"

        override fun evaluate(value: String): String = value.uppercase()
    }

    private val multilineExpressionContext = object : CascExpressionContext {

        override val name: String = "multiline"

        private val expressions = mapOf(
            "test" to """
                A clean
                multiline
                string
            """.trimIndent()
        )

        override fun evaluate(value: String): String = expressions[value] ?: error("Cannot find multiline value for $value")
    }

    private val expressionPreprocessor = CascExpressionPreprocessor(
        listOf(
            mockExpressionContext,
            multilineExpressionContext,
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

    @Test
    fun `Multiline expression`() {
        assertEquals(
            """
                value: |-
                    A clean
                    multiline
                    string
            """.trimIndent(),
            expressionPreprocessor.process("""
                value: |-
                    {{ multiline.test }}
            """.trimIndent())
        )
    }

}
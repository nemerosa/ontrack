package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingMisconfiguredConfigParamException
import net.nemerosa.ontrack.model.templating.TemplatingMissingConfigParam
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SinceTemplatingFunctionTest {

    private lateinit var sinceTemplatingFunction: SinceTemplatingFunction
    private lateinit var expressionResolver: (expression: String) -> String
    private lateinit var resolvedExpressions: MutableMap<String, String>

    @BeforeEach
    fun setUp() {
        resolvedExpressions = mutableMapOf()
        expressionResolver = { expression ->
            resolvedExpressions[expression] ?: "#error"
        }
        sinceTemplatingFunction = SinceTemplatingFunction()
    }

    @Test
    fun `From parameter is required`() {
        assertFailsWith<TemplatingMissingConfigParam> {
            sinceTemplatingFunction.render(
                configMap = emptyMap(),
                context = emptyMap(),
                renderer = PlainEventRenderer.INSTANCE,
                expressionResolver = expressionResolver,
            )
        }
    }

    @Test
    fun `Format parameter must be seconds or minutes`() {
        assertFailsWith<TemplatingMisconfiguredConfigParamException> {
            sinceTemplatingFunction.render(
                configMap = mapOf(
                    "format" to "minutes",
                    "from" to "test",
                ),
                context = emptyMap(),
                renderer = PlainEventRenderer.INSTANCE,
                expressionResolver = expressionResolver,
            )
        }
    }

    @Test
    fun `Duration with current time as reference`() {
        val ref = Time.now
        val from = ref.minusMinutes(1)
        resolvedExpressions["test"] = Time.store(from)
        val text = sinceTemplatingFunction.render(
            configMap = mapOf(
                "format" to "seconds",
                "from" to "test",
            ),
            context = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
            expressionResolver = expressionResolver,
        )
        // ~ roughly one minute (not exact because using current time)
        assertTrue(
            text in setOf("59", "60", "61"),
            "One minute"
        )
    }

    @Test
    fun `Duration with custom reference`() {
        val ref = Time.now
        val from = ref.minusMinutes(2)
        val to = ref.minusMinutes(1)
        resolvedExpressions["from"] = Time.store(from)
        resolvedExpressions["ref"] = Time.store(to)
        val text = sinceTemplatingFunction.render(
            configMap = mapOf(
                "from" to "from",
                "ref" to "ref",
            ),
            context = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
            expressionResolver = expressionResolver,
        )
        assertEquals("60", text)
    }

    @Test
    fun `Duration in ms with custom reference`() {
        val ref = Time.now
        val from = ref.minusMinutes(2)
        val to = ref.minusMinutes(1)
        resolvedExpressions["from"] = Time.store(from)
        resolvedExpressions["ref"] = Time.store(to)
        val text = sinceTemplatingFunction.render(
            configMap = mapOf(
                "format" to "millis",
                "from" to "from",
                "ref" to "ref",
            ),
            context = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
            expressionResolver = expressionResolver,
        )
        assertEquals("60000", text)
    }

}
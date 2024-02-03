package net.nemerosa.ontrack.service.templating

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingMissingConfigParam
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LinkTemplatingFunctionTest {

    private lateinit var linkTemplatingFunction: LinkTemplatingFunction
    private lateinit var eventRenderer: EventRenderer
    private lateinit var expressionResolver: (expression: String) -> String

    @BeforeEach
    fun init() {
        linkTemplatingFunction = LinkTemplatingFunction()

        eventRenderer = mockk()

        val resolvedExpressions = mapOf(
            "SOME_TEXT" to "My text",
            "SOME_HREF" to "https://some.link",
        )

        expressionResolver = { expression ->
            resolvedExpressions[expression] ?: "#error"
        }

    }

    @Test
    fun `Missing text`() {
        assertFailsWith<TemplatingMissingConfigParam> {
            linkTemplatingFunction.render(
                configMap = emptyMap(),
                context = emptyMap(),
                renderer = eventRenderer,
                expressionResolver = expressionResolver,
            )
        }
    }

    @Test
    fun `Missing link`() {
        assertFailsWith<TemplatingMissingConfigParam> {
            linkTemplatingFunction.render(
                configMap = mapOf(
                    "text" to "SOME_TEXT",
                ),
                context = emptyMap(),
                renderer = eventRenderer,
                expressionResolver = expressionResolver,
            )
        }
    }

    @Test
    fun `Link rendering`() {
        every { eventRenderer.renderLink("My text", "https://some.link") } returns
                """<a href="https://some.link">My text</a>"""
        val renderedText = linkTemplatingFunction.render(
            configMap = mapOf(
                "text" to "SOME_TEXT",
                "href" to "SOME_HREF",
            ),
            context = emptyMap(),
            renderer = eventRenderer,
            expressionResolver = expressionResolver,
        )
        assertEquals("""<a href="https://some.link">My text</a>""", renderedText)
        verify {
            eventRenderer.renderLink("My text", "https://some.link")
        }
    }

}
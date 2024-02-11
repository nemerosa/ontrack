package net.nemerosa.ontrack.model.events

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlainEventRendererTest {

    private val renderer = PlainEventRenderer.INSTANCE

    @Test
    fun `Rendering space`() {
        assertEquals(
            """
                |One
                |
                |Two
            """.trimMargin(),
            "One${renderer.renderSpace()}Two"
        )
    }

    @Test
    fun `Rendering section`() {
        assertEquals(
            """
                |My title
                |
                |My content
            """.trimMargin(),
            renderer.renderSection(
                "My title",
                "My content"
            )
        )
    }

}
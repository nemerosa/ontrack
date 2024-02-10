package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MarkdownEventRendererTest {

    private val renderer = MarkdownEventRenderer(
        OntrackConfigProperties()
    )

    @Test
    fun `Rendering space`() {
        assertEquals(
            """
One

Two
""",
            """
One${renderer.renderSpace()}Two
"""
        )
    }

    @Test
    fun `Rendering section`() {
        assertEquals(
            """
                |## My title
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
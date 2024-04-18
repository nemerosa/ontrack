package net.nemerosa.ontrack.extension.jira.notifications

import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JiraNotificationEventRendererTest {

    private val renderer = JiraNotificationEventRenderer(
        OntrackConfigProperties()
    )

    @Test
    fun `Render link`() {
        assertEquals(
            """[Text|https://host?query=one&param=value]""",
            renderer.renderLink("Text", "https://host?query=one&param=value")
        )
    }

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
                |h3. My title
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
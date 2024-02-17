package net.nemerosa.ontrack.extension.notifications.rendering

import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HtmlNotificationEventRendererTest {

    private val htmlNotificationEventRenderer = HtmlNotificationEventRenderer(
        OntrackConfigProperties().apply {
            url = "https://ontrack.nemerosa.net"
        }
    )

    @Test
    fun `Value rendering`() {
        assertEquals(
            "<b>Some value</b>",
            htmlNotificationEventRenderer.renderStrong("Some value")
        )
    }

    @Test
    fun `Link rendering`() {
        val text = htmlNotificationEventRenderer.renderLink("PRJ", "https://ontrack.nemerosa.net/#/project/1")
        assertEquals(
            """<a href="https://ontrack.nemerosa.net/#/project/1">PRJ</a>""",
            text
        )
    }

    @Test
    fun `Branch link`() {
        val branch = BranchFixtures.testBranch()
        val text = htmlNotificationEventRenderer.render(branch, branch.name)
        assertEquals(
            """<a href="https://ontrack.nemerosa.net/#/branch/${branch.id}">${branch.name}</a>""",
            text
        )
    }

    @Test
    fun `Rendering space`() {
        assertEquals(
            """
                One<br/><br/>Two
            """.trimIndent(),
            """
                One${htmlNotificationEventRenderer.renderSpace()}Two
            """.trimIndent()
        )
    }

    @Test
    fun `Rendering section`() {
        assertEquals(
            """
                <h3>My title</h3>
                <div>
                    My content
                </div>
            """.trimIndent(),
            htmlNotificationEventRenderer.renderSection(
                "My title",
                "My content"
            )
        )
    }

}
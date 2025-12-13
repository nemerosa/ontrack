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
    fun `Value rendering escaped for HTML`() {
        assertEquals(
            "<b>Some &lt;real&gt; <i>value</i></b>",
            htmlNotificationEventRenderer.renderStrong("Some <real> <i>value</i>")
        )
    }

    @Test
    fun `List rendering`() {
        assertEquals(
            """
                <ul>
                    <li><b>Some value</b></li>
                    <li>Very &lt;wrong&gt;</li>
                    <li>Very plain</li>
                </ul>
            """.trimIndent().lines().map { it.trim() },
            htmlNotificationEventRenderer.renderList(
                listOf(
                    "<b>Some value</b>",
                    "Very <wrong>",
                    "Very plain",
                )
            ).lines().map { it.trim() }
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
    fun `Link rendering with text escaped for HTML`() {
        val text = htmlNotificationEventRenderer.renderLink(
            "<PRJ> is <i>strong</i>",
            "https://ontrack.nemerosa.net/#/project/1"
        )
        assertEquals(
            """<a href="https://ontrack.nemerosa.net/#/project/1">&lt;PRJ&gt; is <i>strong</i></a>""",
            text
        )
    }

    @Test
    fun `Branch link`() {
        val branch = BranchFixtures.testBranch()
        val text = htmlNotificationEventRenderer.render(branch, branch.name)
        assertEquals(
            """<a href="https://ontrack.nemerosa.net/branch/${branch.id}">${branch.name}</a>""",
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

    @Test
    fun `Rendering section with escaped HTML`() {
        assertEquals(
            """
                <h3>My title</h3>
                <div>
                    My content with a <a href="https://ontrack.test.com">link</a> and something &lt;wrong&gt;
                </div>
            """.trimIndent(),
            htmlNotificationEventRenderer.renderSection(
                "My title",
                """My content with a <a href="https://ontrack.test.com">link</a> and something <wrong>"""
            )
        )
    }

    @Test
    fun `Rendering section with lists`() {
        assertEquals(
            """
                <h3>My title</h3>
                <div>
                    <ul>
                        <li>My content with a <a href="https://ontrack.test.com">link</a> and something &lt;wrong&gt;</li>
                        <li>Some <b>bold</b> content</li>
                    </ul>
                </div>
            """.trimIndent().lines().map { it.trim() },
            htmlNotificationEventRenderer.renderSection(
                "My title",
                """
                    <ul>
                        <li>My content with a <a href="https://ontrack.test.com">link</a> and something <wrong></li>
                        <li>Some <b>bold</b> content</li>
                    </ul>
                """
            ).lines().map { it.trim() }
        )
    }

}
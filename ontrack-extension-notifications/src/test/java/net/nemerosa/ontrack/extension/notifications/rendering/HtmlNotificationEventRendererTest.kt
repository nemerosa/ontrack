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
        val text = htmlNotificationEventRenderer.render(branch)
        assertEquals(
            """<a href="https://ontrack.nemerosa.net/#/branch/${branch.id}">${branch.name}</a>""",
            text
        )
    }

}
package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SlackNotificationEventRendererTest {

    private val slackNotificationEventRenderer = SlackNotificationEventRenderer(
        OntrackConfigProperties().apply {
            url = "https://ontrack.nemerosa.net"
        }
    )

    @Test
    fun `Value rendering`() {
        assertEquals(
            "**Some value**",
            slackNotificationEventRenderer.renderStrong("Some value")
        )
    }

    @Test
    fun `Link rendering`() {
        val text = slackNotificationEventRenderer.renderLink("PRJ", "https://ontrack.nemerosa.net/#/project/1")
        assertEquals(
            """<https://ontrack.nemerosa.net/#/project/1|PRJ>""",
            text
        )
    }

    @Test
    fun `Branch link`() {
        val branch = BranchFixtures.testBranch()
        val text = slackNotificationEventRenderer.render(branch)
        assertEquals(
            """<https://ontrack.nemerosa.net/#/branch/${branch.id}|${branch.name}>""",
            text
        )
    }

}
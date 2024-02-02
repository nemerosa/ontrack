package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.it.events.AbstractEventRendererTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SlackNotificationEventRendererIT : AbstractEventRendererTestSupport() {

    @Autowired
    private lateinit var slackNotificationEventRenderer: SlackNotificationEventRenderer

    @Test
    fun `Rendering of entity links with the Slack renderer`() {
        testEntityLinksAndBuildRelease(
            renderer = slackNotificationEventRenderer,
            expectedText = """
                                Build ${'$'}{build.release} for ${'$'}{branch} at ${'$'}{project}
                                has been promoted to ${'$'}{promotion}.
                            """.trimIndent()
        )
    }

}
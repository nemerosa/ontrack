package net.nemerosa.ontrack.kdsl.acceptance.tests.slack

import net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.AbstractACCDSLNotificationsTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.slack.SlackSettings
import net.nemerosa.ontrack.kdsl.spec.extension.slack.mock.mock
import net.nemerosa.ontrack.kdsl.spec.extension.slack.slack
import net.nemerosa.ontrack.kdsl.spec.settings.settings
import org.junit.jupiter.api.Test

class ACCDSLSlackNotifications : AbstractACCDSLNotificationsTestSupport() {

    @Test
    fun `Slack message sent on promotion with parameters`() {

        ontrack.settings.slack.set(
            SlackSettings(
                enabled = true,
                token = "mock",
            )
        )

        val channel = uid("#")

        project {
            branch {
                val pl = promotion()
                pl.subscribe(
                    name = "Test",
                    channel = "slack",
                    channelConfig = mapOf(
                        "channel" to channel,
                        "type" to "SUCCESS",
                    ),
                    keywords = null,
                    events = listOf(
                        "new_promotion_run"
                    )
                )
                build {
                    promote(pl.name)
                    // Checks that the job was called
                    waitUntil(
                        timeout = 10_000,
                        interval = 500L,
                    ) {
                        val messages = ontrack.slack.mock.getChannelMessages(channel)
                        messages.forEach {
                            println("[slack] ${it.markdown}")
                        }
                        messages.any {
                            it.markdown == """Build <http://localhost:8080/#/build/${id}|$name> has been promoted to <http://localhost:8080/#/promotionLevel/${pl.id}|${pl.name}> for branch <http://localhost:8080/#/branch/${branch.id}|${branch.name}> in <http://localhost:8080/#/project/${project.id}|${project.name}>."""
                        }
                    }
                }
            }
        }
    }

}
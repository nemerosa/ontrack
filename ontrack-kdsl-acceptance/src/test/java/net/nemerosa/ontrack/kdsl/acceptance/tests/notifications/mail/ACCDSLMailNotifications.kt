package net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.mail

import net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.AbstractACCDSLNotificationsTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.mail.mail
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Test

class ACCDSLMailNotifications : AbstractACCDSLNotificationsTestSupport() {

    @Test
    fun `Sending a notification by mail`() {
        val subject = uid("sub_")
        project {
            branch {
                val pl = promotion()
                // Subscribe to this promotion
                pl.subscribe(
                    name = "Test",
                    channel = "mail",
                    channelConfig = mapOf(
                        "to" to "info@nemerosa.com",
                        "subject" to """$subject - Version ${'$'}{build} has been promoted to ${'$'}{promotionLevel}"""
                    ),
                    keywords = null,
                    events = listOf(
                        "new_promotion_run",
                    ),
                )
                // Build to promote
                build {
                    // Promotion
                    promote(pl.name)
                    // Checks that a mail was received
                    waitUntil(
                        timeout = 10_000,
                        interval = 500L,
                    ) {
                        val mail = ontrack.notifications.mail.findMailBy(
                            to = "info@nemerosa.com",
                            subject = "^$subject - .*"
                        )
                        mail != null && mail.subject == "$subject - Version $name has been promoted to ${pl.name}"
                    }
                }
            }
        }
    }

}
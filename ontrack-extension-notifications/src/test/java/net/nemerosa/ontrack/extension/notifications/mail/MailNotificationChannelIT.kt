package net.nemerosa.ontrack.extension.notifications.mail

import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertNotNull

/**
 * Testing the availability of the mail channel.
 */
@TestPropertySource(
    properties = [
        "spring.mail.host=stmp.ontrack.run",
    ]
)
class MailNotificationChannelIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var notificationChannelRegistry: NotificationChannelRegistry

    @Test
    fun `Mail channel is available`() {
        // Since spring.mail.host has been set,
        // we expect the mail channel to be available
        val mail = notificationChannelRegistry.channels.find { it.type == "mail" }
        assertNotNull(mail, "Mail channel is available")
    }

}
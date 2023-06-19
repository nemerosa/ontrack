package net.nemerosa.ontrack.extension.notifications.settings

import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getRequiredBooleanField
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertTrue

class NotificationsGraphQLControllerIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var notificationsConfigProperties: NotificationsConfigProperties

    @Test
    fun `Notification settings`() {
        val old = notificationsConfigProperties.enabled
        try {
            notificationsConfigProperties.enabled = true
            run(
                """
                {
                    notificationSettings {
                        enabled
                    }
                }
            """
            ) { data ->
                assertTrue(
                    data.path("notificationSettings").getRequiredBooleanField("enabled"),
                    "Notifications are enabled"
                )
            }
        } finally {
            notificationsConfigProperties.enabled = old
        }
    }

}
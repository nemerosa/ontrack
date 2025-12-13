package net.nemerosa.ontrack.extension.notifications.ci

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannel
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NotificationsCIConfigExtensionTest {

    private lateinit var notificationChannelRegistry: NotificationChannelRegistry
    private lateinit var extension: NotificationsCIConfigExtension

    @BeforeEach
    fun before() {

        val mockChannel = MockNotificationChannel(
            eventTemplatingService = mockk(),
            eventRendererRegistry = mockk(),
        )

        notificationChannelRegistry = mockk()
        every { notificationChannelRegistry.findChannel("mock") } returns mockChannel

        extension = NotificationsCIConfigExtension(
            notificationsExtensionFeature = mockk(),
            eventSubscriptionService = mockk(),
            structureService = mockk(),
            notificationChannelRegistry = notificationChannelRegistry,
        )
    }

    @Test
    fun `Merging of subscriptions`() {
        val defaults = NotificationsCIConfig(
            notifications = listOf(
                NotificationsCIConfigItem(
                    name = "Sub1",
                    promotion = "BRONZE",
                    events = listOf("new_promotion_run"),
                    keywords = null,
                    channel = "mock",
                    channelConfig = mapOf("target" to "#notifications").asJson(),
                ),
                NotificationsCIConfigItem(
                    name = "Sub2",
                    promotion = "RELEASE",
                    events = listOf("new_promotion_run"),
                    keywords = null,
                    channel = "mock",
                    channelConfig = mapOf("target" to "#internal").asJson(),
                ),
            )
        )
        val custom = NotificationsCIConfig(
            notifications = listOf(
                NotificationsCIConfigItem(
                    name = "Sub2",
                    channelConfig = mapOf("target" to "#release").asJson(),
                ),
            )
        )

        val config = extension.mergeData(defaults, custom)

        assertEquals(
            NotificationsCIConfig(
                notifications = listOf(
                    NotificationsCIConfigItem(
                        name = "Sub1",
                        promotion = "BRONZE",
                        events = listOf("new_promotion_run"),
                        keywords = null,
                        channel = "mock",
                        channelConfig = mapOf("target" to "#notifications").asJson(),
                    ),
                    NotificationsCIConfigItem(
                        name = "Sub2",
                        promotion = "RELEASE",
                        events = listOf("new_promotion_run"),
                        keywords = null,
                        channel = "mock",
                        channelConfig = mapOf(
                            "target" to "#release",
                            "data" to null,
                            "rendererType" to null,
                        ).asJson(),
                    ),
                )
            ),
            config
        )

    }

}
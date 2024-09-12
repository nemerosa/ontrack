package net.nemerosa.ontrack.extension.slack.notifications

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.extension.slack.SlackSettings
import net.nemerosa.ontrack.extension.slack.service.SlackNotificationType
import net.nemerosa.ontrack.extension.slack.service.SlackService
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectFixtures
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SlackNotificationChannelTest {

    private lateinit var project: Project
    private lateinit var event: Event

    private lateinit var eventTemplatingService: EventTemplatingService
    private lateinit var cachedSettingsService: CachedSettingsService
    private lateinit var slackService: SlackService
    private lateinit var channel: SlackNotificationChannel
    private lateinit var slackNotificationEventRenderer: SlackNotificationEventRenderer

    @BeforeEach
    fun before() {
        slackService = mockk()
        cachedSettingsService = mockk()

        project = ProjectFixtures.testProject(name = uid("prj-"))
        event = Event.of(EventFactory.DISABLE_PROJECT).withProject(project).build()

        slackNotificationEventRenderer = SlackNotificationEventRenderer(OntrackConfigProperties())

        eventTemplatingService = mockk()
        every {
            eventTemplatingService.renderEvent(
                event = event,
                template = null,
                context = emptyMap(),
                renderer = slackNotificationEventRenderer,
            )
        } returns event.eventType.template

        channel = SlackNotificationChannel(
            slackService,
            cachedSettingsService,
            slackNotificationEventRenderer,
            eventTemplatingService,
        )

    }

    @Test
    fun `Using the event renderer to format the message`() {
        every { cachedSettingsService.getCachedSettings(SlackSettings::class.java) } returns SlackSettings(
            enabled = true
        )

        every { slackService.sendNotification(any(), any(), any()) } returns true
        val config = SlackNotificationChannelConfig(channel = "#test", type = SlackNotificationType.SUCCESS)
        val result = channel.publish(
            recordId = "1",
            config = config,
            event = event,
            context = emptyMap(),
            template = null,
            outputProgressCallback = { it }
        )
        verify {
            slackService.sendNotification(
                "#test",
                event.eventType.template,
                SlackNotificationType.SUCCESS
            )
        }
        assertEquals(NotificationResultType.OK, result.type)
        assertNull(result.message)
    }

    @Test
    fun `Returning an error when the Slack message cannot be sent`() {
        every { cachedSettingsService.getCachedSettings(SlackSettings::class.java) } returns SlackSettings(
            enabled = true
        )
        every { slackService.sendNotification(any(), any(), any()) } returns false // <== returning an error
        val config = SlackNotificationChannelConfig(channel = "#test")
        val result = channel.publish(
            recordId = "1",
            config,
            event,
            context = emptyMap(),
            template = null,
            outputProgressCallback = { it }
        )
        assertEquals(NotificationResultType.ERROR, result.type)
        assertEquals("Slack message could not be sent. Check the operational logs.", result.message)
    }

    @Test
    fun `Channel enabled if Slack settings are enabled`() {
        every { cachedSettingsService.getCachedSettings(SlackSettings::class.java) } returns SlackSettings(
            enabled = true
        )
        assertTrue(channel.enabled, "Channel is disabled")
    }

    @Test
    fun `Channel disabled if Slack settings are disabled`() {
        every { cachedSettingsService.getCachedSettings(SlackSettings::class.java) } returns SlackSettings(
            enabled = false
        )
        assertFalse(channel.enabled, "Channel is disabled")
    }

}
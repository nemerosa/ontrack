package net.nemerosa.ontrack.extension.slack.notifications

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.extension.slack.SlackSettings
import net.nemerosa.ontrack.extension.slack.service.SlackService
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SlackNotificationChannelTest {

    private lateinit var project: Project
    private lateinit var event: Event

    private lateinit var eventRenderer: EventRenderer
    private lateinit var cachedSettingsService: CachedSettingsService
    private lateinit var slackService: SlackService
    private lateinit var channel: SlackNotificationChannel

    @BeforeEach
    fun before() {
        slackService = mockk()
        cachedSettingsService = mockk()
        eventRenderer = mockk()
        channel = SlackNotificationChannel(
            slackService,
            cachedSettingsService,
            eventRenderer,
        )

        project = Project.of(NameDescription.nd("project", "Test project")).withId(ID.of(1))
        event = Event.of(EventFactory.DISABLE_PROJECT).withProject(project).get()
    }

    @Test
    fun `Using the event renderer to format the message`() {
        every { cachedSettingsService.getCachedSettings(SlackSettings::class.java) } returns SlackSettings(
            enabled = true
        )
        every { eventRenderer.render(project, event) } returns project.name
        every { slackService.sendNotification(any(), any()) } returns true
        val config = SlackNotificationChannelConfig(channel = "#test")
        val result = channel.publish(config, event)
        verify {
            eventRenderer.render(project, event)
        }
        verify {
            slackService.sendNotification("#test", "Project project has been disabled.", null)
        }
        assertEquals(NotificationResultType.OK, result.type)
        assertNull(result.message)
    }

    @Test
    fun `Returning an error when the Slack message cannot be sent`() {
        every { cachedSettingsService.getCachedSettings(SlackSettings::class.java) } returns SlackSettings(
            enabled = true
        )
        every { eventRenderer.render(project, event) } returns project.name
        every { slackService.sendNotification(any(), any()) } returns false // <== returning an error
        val config = SlackNotificationChannelConfig(channel = "#test")
        val result = channel.publish(config, event)
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
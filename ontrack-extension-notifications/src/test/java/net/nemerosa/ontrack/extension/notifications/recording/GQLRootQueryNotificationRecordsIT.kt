package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelOutput
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredIntField
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.test.assertJsonNotNull
import net.nemerosa.ontrack.test.assertJsonNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

internal class GQLRootQueryNotificationRecordsIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var notificationRecordingService: NotificationRecordingService

    @Test
    fun `Getting the list of notifications`() {
        asAdmin {
            notificationRecordingService.clearAll()
            // One event
            val project = project {}
            val event = eventFactory.newProject(project)
            // Records a notification
            notificationRecordingService.record(
                NotificationRecord(
                    id = UUID.randomUUID().toString(),
                    timestamp = Time.now(),
                    channel = "mock",
                    channelConfig = MockNotificationChannelConfig("#target").asJson(),
                    event = event.asJson(),
                    result = NotificationResult.ok(
                        output = MockNotificationChannelOutput(text = "The actual text")
                    ).toNotificationRecordResult(),
                )
            )
            // Getting this notification
            run("""{
                notificationRecords {
                    pageItems {
                        timestamp
                        channel
                        channelConfig
                        event
                        result {
                            type
                            message
                        }
                    }
                }
            }""") { data ->
                assertJsonNotNull(data.path("notificationRecords").path("pageItems").path(0)) {
                    assertEquals("mock", getRequiredTextField("channel"))
                    assertEquals("#target", path("channelConfig").getRequiredTextField("target"))
                    assertEquals(project.id(), path("event").path("entities").path("PROJECT").getRequiredIntField("id"))
                    assertEquals("OK", path("result").getRequiredTextField("type"))
                    assertJsonNull(path("result").path("message"))
                }
            }
        }
    }

    @Test
    fun `Getting the list of notifications filtered by result type`() {
        asAdmin {
            notificationRecordingService.clearAll()
            // One event
            val project = project {}
            val event = eventFactory.newProject(project)
            // Records a OK notification
            notificationRecordingService.record(
                NotificationRecord(
                    id = UUID.randomUUID().toString(),
                    timestamp = Time.now(),
                    channel = "mock",
                    channelConfig = MockNotificationChannelConfig("#target").asJson(),
                    event = event.asJson(),
                    result = NotificationResult.ok(
                        output = MockNotificationChannelOutput(text = "The actual text")
                    ).toNotificationRecordResult(),
                )
            )
            // Records a misconfigured notification
            notificationRecordingService.record(
                NotificationRecord(
                    id = UUID.randomUUID().toString(),
                    timestamp = Time.now(),
                    channel = "mock",
                    channelConfig = MockNotificationChannelConfig("not-valid").asJson(),
                    event = event.asJson(),
                    result = NotificationResult.invalidConfiguration<MockNotificationChannelOutput>().toNotificationRecordResult()
                )
            )
            // Getting this notification
            run("""{
                notificationRecords(
                    resultType: INVALID_CONFIGURATION
                ) {
                    pageItems {
                        timestamp
                        channel
                        channelConfig
                        event
                        result {
                            type
                            message
                        }
                    }
                }
            }""") { data ->
                assertJsonNotNull(data.path("notificationRecords").path("pageItems").path(0)) {
                    assertEquals("mock", getRequiredTextField("channel"))
                    assertEquals("not-valid", path("channelConfig").getRequiredTextField("target"))
                    assertEquals(project.id(), path("event").path("entities").path("PROJECT").getRequiredIntField("id"))
                    assertEquals("INVALID_CONFIGURATION", path("result").getRequiredTextField("type"))
                    assertEquals("Invalid configuration", path("result").getTextField("message"))
                }
            }
        }
    }

}
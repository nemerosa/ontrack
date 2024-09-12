package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.extension.notifications.sources.EntitySubscriptionNotificationSourceDataType
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NotificationRecordsIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var notificationRecordingService: NotificationRecordingService

    @Test
    fun `Getting the notifications linked to a promotion run`() {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    // Subscription at promotion level
                    val target = uid("pl-")
                    val subscriptionName = uid("pl-sub-")
                    eventSubscriptionService.subscribe(
                        name = subscriptionName,
                        channel = mockNotificationChannel,
                        channelConfig = MockNotificationChannelConfig(target),
                        projectEntity = pl,
                        keywords = null,
                        origin = "test",
                        contentTemplate = null,
                        EventFactory.NEW_PROMOTION_RUN
                    )
                    // Creating a separate promotion
                    build {
                        promote(pl)
                    }
                    // Tracking a specific promotion
                    build {
                        val run = promote(pl)
                        // Looking at the records for THIS run
                        val records = notificationRecordingService.filter(
                            filter = NotificationRecordFilter(
                                eventEntityId = run.toProjectEntityID(),
                            )
                        ).pageItems
                        assertEquals(1, records.size)
                        val record = records.first()
                        assertEquals(
                            "entity-subscription",
                            record.source?.id
                        )
                        assertEquals(
                            EntitySubscriptionNotificationSourceDataType(
                                entityType = ProjectEntityType.PROMOTION_LEVEL,
                                entityId = pl.id(),
                                subscriptionName = subscriptionName,
                            ).asJson(),
                            record.source?.data
                        )

                        // Getting the same record using its ID
                        assertNotNull(notificationRecordingService.findRecordById(record.id), "Getting record by ID") {
                            assertEquals(record.id, it.id)
                        }
                    }
                }
            }
        }
    }

}
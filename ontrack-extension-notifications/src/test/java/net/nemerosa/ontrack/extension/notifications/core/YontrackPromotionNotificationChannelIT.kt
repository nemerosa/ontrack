package net.nemerosa.ontrack.extension.notifications.core

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordingService
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AsAdminTest
class YontrackPromotionNotificationChannelIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var yontrackPromotionNotificationChannel: YontrackPromotionNotificationChannel

    @Autowired
    private lateinit var notificationRecordingService: NotificationRecordingService

    @Test
    fun `Promotion using a notification using full parameters`() {
        project {
            branch {
                val pl = promotionLevel()
                val targetPl = promotionLevel()

                eventSubscriptionService.subscribe(
                    name = uid("s"),
                    projectEntity = pl,
                    channel = yontrackPromotionNotificationChannel,
                    channelConfig = YontrackPromotionNotificationChannelConfig(
                        project = $$"${project}",
                        branch = $$"${branch}",
                        build = $$"${build}",
                        promotion = targetPl.name,
                    ),
                    keywords = null,
                    origin = "test",
                    contentTemplate = $$"Promotion of ${build}",
                    eventTypes = arrayOf(EventFactory.NEW_PROMOTION_RUN),
                )

                build {
                    promote(pl)

                    // Checks that the target promotion has been set
                    val run = structureService.getPromotionRunsForBuildAndPromotionLevel(this, targetPl).firstOrNull()
                    assertNotNull(run, "Build has been promoted") {
                        assertEquals(
                            "Promotion of $name",
                            it.description
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion using a notification using only required parameters`() {
        project {
            branch {
                val pl = promotionLevel()
                val targetPl = promotionLevel()

                eventSubscriptionService.subscribe(
                    name = uid("s"),
                    projectEntity = pl,
                    channel = yontrackPromotionNotificationChannel,
                    channelConfig = YontrackPromotionNotificationChannelConfig(
                        promotion = targetPl.name,
                    ),
                    keywords = null,
                    origin = "test",
                    contentTemplate = $$"Promotion of ${build}",
                    eventTypes = arrayOf(EventFactory.NEW_PROMOTION_RUN),
                )

                build {
                    promote(pl)

                    // Checks that the target promotion has been set
                    val run = structureService.getPromotionRunsForBuildAndPromotionLevel(this, targetPl).firstOrNull()
                    assertNotNull(run, "Build has been promoted") {
                        assertEquals(
                            "Promotion of $name",
                            it.description
                        )
                    }
                }
            }
        }
    }

}
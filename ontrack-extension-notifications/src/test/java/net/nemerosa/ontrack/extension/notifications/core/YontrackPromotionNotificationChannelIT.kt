package net.nemerosa.ontrack.extension.notifications.core

import net.nemerosa.ontrack.common.waitFor
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordFilter
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordingService
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

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

    @Test
    fun `Waiting for the promotion notifications to be completed and successful`() {

        val targetMockChannel = uid("tc-")

        project {
            branch {
                val targetPl = promotionLevel()
                val targetBuild = build()

                eventSubscriptionService.subscribe(
                    name = uid("t"),
                    projectEntity = targetPl,
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(
                        target = targetMockChannel,
                        waitMs = 500L,
                    ),
                    keywords = null,
                    origin = "test",
                    contentTemplate = $$"Promotion of ${build}",
                    eventTypes = arrayOf(EventFactory.NEW_PROMOTION_RUN),
                )

                project {
                    branch {
                        val pl = promotionLevel()

                        eventSubscriptionService.subscribe(
                            name = uid("s"),
                            projectEntity = pl,
                            channel = yontrackPromotionNotificationChannel,
                            channelConfig = YontrackPromotionNotificationChannelConfig(
                                project = targetBuild.project.name,
                                build = targetBuild.name,
                                promotion = targetPl.name,
                                waitForPromotion = true,
                                waitForPromotionTimeout = 5.seconds.toJavaDuration(),
                            ),
                            keywords = null,
                            origin = "test",
                            contentTemplate = $$"Promotion of ${build}",
                            eventTypes = arrayOf(EventFactory.NEW_PROMOTION_RUN),
                        )

                        build {
                            val sourceRun = promote(pl)

                            // Checks that the target promotion has been set
                            val targetRun = structureService.getPromotionRunsForBuildAndPromotionLevel(targetBuild, targetPl)
                                .firstOrNull()
                            assertNotNull(targetRun, "Build has been promoted") {
                                assertEquals(
                                    "Promotion of $name",
                                    it.description
                                )
                            }

                            // Waits until the Promotion notification is OK
                            waitFor(
                                message = "Waiting for the promotion notification to be completed",
                                interval = 100.milliseconds,
                                timeout = 5.seconds,
                            ) {
                                notificationRecordingService.filter(
                                    filter = NotificationRecordFilter(
                                        eventEntityId = sourceRun.toProjectEntityID(),
                                    )
                                ).pageItems.firstOrNull()
                            } until { record ->
                                record.result.type == NotificationResultType.OK
                            }

                            // Checks that the target notification has been received
                            assertNotNull(mockNotificationChannel.messages[targetMockChannel]) {
                                assertEquals(
                                    "Promotion of ${targetBuild.name}",
                                    it.first()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}
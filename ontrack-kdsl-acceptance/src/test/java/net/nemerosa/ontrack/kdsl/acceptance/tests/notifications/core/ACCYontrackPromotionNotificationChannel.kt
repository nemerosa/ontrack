package net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.core

import net.nemerosa.ontrack.common.waitFor
import net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.AbstractACCDSLNotificationsTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ACCYontrackPromotionNotificationChannel : AbstractACCDSLNotificationsTestSupport() {

    @Test
    fun `Waiting for the promotion notifications to be completed and successful`() {

        val targetGroup = uid("tg-")

        project {
            branch {
                val targetPl = promotion("pl-target")
                val targetBuild = build("target") { this }

                targetPl.subscribe(
                    name = uid("t"),
                    channel = "in-memory",
                    channelConfig = mapOf(
                        "group" to targetGroup,
                        "waitMs" to 1_500L,
                    ),
                    keywords = null,
                    contentTemplate = $$"Promotion of ${build}",
                    events = listOf("new_promotion_run"),
                )

                project {
                    branch {
                        val pl = promotion("pl-source")

                        pl.subscribe(
                            name = uid("s"),
                            channel = "yontrack-promotion",
                            channelConfig = mapOf(
                                "project" to targetBuild.branch.project.name,
                                "build" to targetBuild.name,
                                "promotion" to targetPl.name,
                                "waitForPromotion" to true,
                                "waitForPromotionTimeout" to "5s",
                            ),
                            keywords = null,
                            contentTemplate = $$"Promotion of ${build}",
                            events = listOf("new_promotion_run"),
                        )

                        build("source") {
                            promote(pl.name)

                            // Checks that the target promotion has been set
                            waitFor(
                                message = "Waiting for the target promotion to be set",
                                interval = 500.milliseconds,
                                timeout = 10.seconds,
                            ) {
                                targetBuild.getPromotionRunsForPromotionLevel(targetPl.name).firstOrNull()
                            } until {
                                it.description == "Promotion of source"
                            }

                            // Waits until the Promotion notification is OK
                            waitFor(
                                message = "Waiting for the promotion notification to be completed",
                                interval = 500.milliseconds,
                                timeout = 10.seconds,
                            ) {
                                ontrack.notifications.notificationRecords("yontrack-promotion").firstOrNull()
                            } until { record ->
                                record.result.type == "OK"
                            }

                            // Checks that the target notification has been received
                            waitFor(
                                message = "Waiting for the target notification to be received",
                                interval = 500.milliseconds,
                                timeout = 10.seconds,
                            ) {
                                ontrack.notifications.inMemory.group(targetGroup).firstOrNull()
                            } until { record ->
                                record == "Promotion of ${targetBuild.name}"
                            }

                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Waiting for the promotion notifications with timeout`() {

        val targetGroup = uid("tg-")

        project {
            branch {
                val targetPl = promotion("pl-target")
                val targetBuild = build("target") { this }

                targetPl.subscribe(
                    name = uid("t"),
                    channel = "in-memory",
                    channelConfig = mapOf(
                        "group" to targetGroup,
                        "waitMs" to 5_000L,
                    ),
                    keywords = null,
                    contentTemplate = $$"Promotion of ${build}",
                    events = listOf("new_promotion_run"),
                )

                project {
                    branch {
                        val pl = promotion("pl-source")

                        pl.subscribe(
                            name = uid("s"),
                            channel = "yontrack-promotion",
                            channelConfig = mapOf(
                                "project" to targetBuild.branch.project.name,
                                "build" to targetBuild.name,
                                "promotion" to targetPl.name,
                                "waitForPromotion" to true,
                                // Waiting at most 2 seconds, but the target promotion notification will take 5 seconds
                                "waitForPromotionTimeout" to "2s",
                            ),
                            keywords = null,
                            contentTemplate = $$"Promotion of ${build}",
                            events = listOf("new_promotion_run"),
                        )

                        build("source") {
                            promote(pl.name)

                            // Checks that the target promotion has been set
                            waitFor(
                                message = "Waiting for the target promotion to be set",
                                interval = 500.milliseconds,
                                timeout = 10.seconds,
                            ) {
                                targetBuild.getPromotionRunsForPromotionLevel(targetPl.name).firstOrNull()
                            } until {
                                it.description == "Promotion of $name"
                            }

                            // Waits until the Promotion notification is OK
                            waitFor(
                                message = "Waiting for the promotion notification to be completed",
                                interval = 500.milliseconds,
                                timeout = 10.seconds,
                            ) {
                                ontrack.notifications.notificationRecords("yontrack-promotion").firstOrNull()
                            } until { record ->
                                // Timeout
                                record.result.type == "ERROR"
                            }

                            // Checks that the target notification has been received all the same
                            waitFor(
                                message = "Waiting for the target notification to be received",
                                interval = 500.milliseconds,
                                timeout = 10.seconds,
                            ) {
                                ontrack.notifications.inMemory.group(targetGroup).firstOrNull()
                            } until { record ->
                                record == "Promotion of ${targetBuild.name}"
                            }

                        }
                    }
                }
            }
        }
    }

    // Cannot work with sync queues
//    @Test
//    fun `Waiting for the promotion notifications with timeout`() {
//
//        val targetMockChannel = uid("tc-")
//
//        project {
//            branch {
//                val targetPl = promotionLevel()
//                val targetBuild = build()
//
//                eventSubscriptionService.subscribe(
//                    name = uid("t"),
//                    projectEntity = targetPl,
//                    channel = mockNotificationChannel,
//                    channelConfig = MockNotificationChannelConfig(
//                        target = targetMockChannel,
//                        waitMs = 5_000L,
//                    ),
//                    keywords = null,
//                    origin = "test",
//                    contentTemplate = $$"Promotion of ${build}",
//                    eventTypes = arrayOf(EventFactory.NEW_PROMOTION_RUN),
//                )
//
//                project {
//                    branch {
//                        val pl = promotionLevel()
//
//                        eventSubscriptionService.subscribe(
//                            name = uid("s"),
//                            projectEntity = pl,
//                            channel = yontrackPromotionNotificationChannel,
//                            channelConfig = YontrackPromotionNotificationChannelConfig(
//                                project = targetBuild.project.name,
//                                build = targetBuild.name,
//                                promotion = targetPl.name,
//                                waitForPromotion = true,
//                                waitForPromotionTimeout = 2.seconds.toJavaDuration(),
//                            ),
//                            keywords = null,
//                            origin = "test",
//                            contentTemplate = $$"Promotion of ${build}",
//                            eventTypes = arrayOf(EventFactory.NEW_PROMOTION_RUN),
//                        )
//
//                        build {
//                            val sourceRun = promote(pl)
//
//                            // Checks that the target promotion has been set
//                            val targetRun = structureService.getPromotionRunsForBuildAndPromotionLevel(targetBuild, targetPl)
//                                .firstOrNull()
//                            assertNotNull(targetRun, "Build has been promoted") {
//                                assertEquals(
//                                    "Promotion of $name",
//                                    it.description
//                                )
//                            }
//
//                            waitFor(
//                                message = "Waiting for the promotion notification to be errored",
//                                interval = 100.milliseconds,
//                                timeout = 5.seconds,
//                            ) {
//                                notificationRecordingService.filter(
//                                    filter = NotificationRecordFilter(
//                                        eventEntityId = sourceRun.toProjectEntityID(),
//                                    )
//                                ).pageItems.firstOrNull()
//                            } until { record ->
//                                record.result.type == NotificationResultType.ERROR
//                            }
//
//                            // Checks that the target notification has been received all the same
//                            assertNotNull(mockNotificationChannel.messages[targetMockChannel]) {
//                                assertEquals(
//                                    "Promotion of ${targetBuild.name}",
//                                    it.first()
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

}
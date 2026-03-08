package net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.core

import net.nemerosa.ontrack.common.waitFor
import net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.AbstractACCDSLNotificationsTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ACCYontrackPromotionNotificationChannel : AbstractACCDSLNotificationsTestSupport() {

    @Test
    fun `Promotion using a notification`() {

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
                            ),
                            keywords = null,
                            contentTemplate = $$"Promotion of ${build}",
                            events = listOf("new_promotion_run"),
                        )

                        build("source") {
                            val sourceRun = promote(pl.name)

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
                                ontrack.notifications.notificationRecords(
                                    channel = "yontrack-promotion",
                                    projectEntityType = ProjectEntityType.PROMOTION_RUN,
                                    projectEntityId = sourceRun.id.toInt(),
                                ).firstOrNull()
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
                            val sourceRun = promote(pl.name)

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
                                ontrack.notifications.notificationRecords(
                                    channel = "yontrack-promotion",
                                    projectEntityType = ProjectEntityType.PROMOTION_RUN,
                                    projectEntityId = sourceRun.id.toInt(),
                                ).firstOrNull()
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
                                "waitForPromotionTimeout" to "2s",
                            ),
                            keywords = null,
                            contentTemplate = $$"Promotion of ${build}",
                            events = listOf("new_promotion_run"),
                        )

                        build("source") {
                            val sourceRun = promote(pl.name)

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

                            // Waits until the Promotion notification is errored
                            waitFor(
                                message = "Waiting for the promotion notification to be errored",
                                interval = 500.milliseconds,
                                timeout = 10.seconds,
                            ) {
                                ontrack.notifications.notificationRecords(
                                    channel = "yontrack-promotion",
                                    projectEntityType = ProjectEntityType.PROMOTION_RUN,
                                    projectEntityId = sourceRun.id.toInt(),
                                ).firstOrNull()
                            } until { record ->
                                record.result.type == "ERROR"
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

}
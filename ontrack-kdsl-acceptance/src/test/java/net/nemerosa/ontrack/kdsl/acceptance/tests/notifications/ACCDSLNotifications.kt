package net.nemerosa.ontrack.kdsl.acceptance.tests.notifications

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ACCDSLNotifications : AbstractACCDSLNotificationsTestSupport() {

    @Test
    fun `Notifications for a project for new promotions with a keyword filter on promotion and branch`() {
        // Group of messages
        val group = uid("g")
        // Creates a project
        project project@{
            // Creates a subscription for this project
            // For new promotion runs
            // For GOLD & main only
            subscribe(
                channel = "in-memory",
                channelConfig = mapOf("group" to group),
                keywords = "GOLD main",
                events = listOf(
                    "new_promotion_run",
                )
            )
            // For non-matching branches
            branch("release-1.1") {
                promotion("SILVER")
                promotion("GOLD")
                build {
                    // Promotion
                    promote("SILVER")
                    // Checks that NO notification was received
                    assertTrue(ontrack.notifications.inMemory.group(group).isEmpty(), "No notification")
                    // Promotion
                    promote("GOLD")
                    // Checks that NO notification was received (branch name is not matching)
                    assertTrue(ontrack.notifications.inMemory.group(group).isEmpty(), "No notification")
                }
            }

            // For matching branches
            branch("main") branch@{
                promotion("SILVER")
                promotion("GOLD")
                build {
                    // Promotion
                    promote("SILVER")
                    // Checks that NO notification was received
                    assertTrue(ontrack.notifications.inMemory.group(group).isEmpty(), "No notification")
                    // Promotion
                    promote("GOLD")
                    // Checks that a notification was received
                    waitUntil(
                        timeout = 30_000,
                        interval = 500L,
                    ) {
                        ontrack.notifications.inMemory.group(group).firstOrNull() ==
                                "Build $name has been promoted to GOLD for branch ${this@branch.name} in ${this@project.name}."
                    }
                }
            }
        }
    }

}
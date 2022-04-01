package net.nemerosa.ontrack.kdsl.acceptance.tests.notifications

import net.nemerosa.ontrack.kdsl.acceptance.annotations.AcceptanceTestSuite
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.ProjectEntity
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Test

@AcceptanceTestSuite
class ACCDSLNotifications : AbstractACCDSLTestSupport() {

    @Test
    fun `Notifications for a project for new promotions with a keyword filter on promotion and branch`() {
        // Group of messages
        val group = uid("g")
        // Creates a project
        project {
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
                    //         // Promotion
                    //         promote("SILVER")
                    //         // Checks that NO notification was received
                    //         assertTrue(ontrack.notifications.inMemory.group(group).isEmpty(), "No notification")
                    //         // Promotion
                    //         promote("GOLD")
                    //         // Checks that NO notification was received (branch name is not matching)
                    //         assertTrue(ontrack.notifications.inMemory.group(group).isEmpty(), "No notification")
                }
            }

            // For matching branches
            branch("main") {
                promotion("SILVER")
                promotion("GOLD")
                build {
                    //         // Promotion
                    //         promote("SILVER")
                    //         // Checks that NO notification was received
                    //         assertTrue(ontrack.notifications.inMemory.group(group).isEmpty(), "No notification")
                    //         // Promotion
                    //         promote("GOLD")
                    //         // Checks that a notification was received
                    //         assertNotNull(ontrack.notifications.inMemory.group(group).firstOrNull(), "Received notification") { message ->
                    //             assertEquals("", message)
                    //         }
                }
            }
        }
    }

    /**
     * Subscription for a project entity.
     */
    fun ProjectEntity.subscribe(
        channel: String,
        channelConfig: Any,
        keywords: String?,
        events: List<String>,
    ) {
        ontrack.notifications.subscribe(
            channel,
            channelConfig,
            keywords,
            events,
            projectEntity = this,
        )
    }

}
package net.nemerosa.ontrack.kdsl.acceptance.tests.notifications

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ACCDSLNotifications : AbstractACCDSLNotificationsTestSupport() {

    @Test
    fun `Notifications are synchronized on name`() {
        project {
            branch {
                val pl = promotion()
                // Subscription using a given group
                pl.subscribe(
                    name = "My subscription",
                    channel = "in-memory",
                    channelConfig = mapOf("group" to "group-1"),
                    keywords = null,
                    events = listOf(
                        "new_promotion_run",
                    ),
                )
                // Checking the subscriptions for this promotion
                val oldList = pl.subscriptions()
                assertEquals(
                    listOf("My subscription"),
                    oldList.map { it.name },
                )
                assertEquals(
                    listOf("group-1"),
                    oldList.map { it.channelConfig.path("group").asText() }
                )
                // Changing the configuration, not the name
                pl.subscribe(
                    name = "My subscription",
                    channel = "in-memory",
                    channelConfig = mapOf("group" to "group-2"),
                    keywords = null,
                    events = listOf(
                        "new_promotion_run",
                    ),
                )
                // Checking the subscriptions for this promotion
                val newList = pl.subscriptions()
                assertEquals(
                    listOf("My subscription"),
                    newList.map { it.name },
                )
                assertEquals(
                    listOf("group-2"),
                    newList.map { it.channelConfig.path("group").asText() }
                )
            }
        }
    }

    @Test
    fun `Notifications for new promotions at promotion level`() {
        val group = uid("g_")
        project {
            branch {
                val pl = promotion()
                // Subscribe to this promotion
                pl.subscribe(
                    name = "Mock notification on promotion",
                    channel = "in-memory",
                    channelConfig = mapOf("group" to group),
                    keywords = null,
                    events = listOf(
                        "new_promotion_run",
                    ),
                )
                // Build to promote
                build {
                    // Promotion
                    promote(pl.name)
                    // Checks that a notification was received
                    waitUntil(
                        timeout = 30_000,
                        interval = 500L,
                    ) {
                        ontrack.notifications.inMemory.group(group).firstOrNull() ==
                                "Build $name has been promoted to ${pl.name} for branch ${branch.name} in ${project.name}."
                    }
                }
            }
        }
    }

    @Test
    fun `Notifications for new promotions at promotion level - no subscriptio name, testing backward compatibility`() {
        val group = uid("g_")
        project {
            branch {
                val pl = promotion()
                // Subscribe to this promotion
                pl.subscribe(
                    name = "Test",
                    channel = "in-memory",
                    channelConfig = mapOf("group" to group),
                    keywords = null,
                    events = listOf(
                        "new_promotion_run",
                    ),
                )
                // Build to promote
                build {
                    // Promotion
                    promote(pl.name)
                    // Checks that a notification was received
                    waitUntil(
                        timeout = 30_000,
                        interval = 500L,
                    ) {
                        ontrack.notifications.inMemory.group(group).firstOrNull() ==
                                "Build $name has been promoted to ${pl.name} for branch ${branch.name} in ${project.name}."
                    }
                }
            }
        }
    }

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
                name = "Test",
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
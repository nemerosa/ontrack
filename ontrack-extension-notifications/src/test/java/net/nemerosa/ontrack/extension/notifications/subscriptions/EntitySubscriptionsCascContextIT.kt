package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.casc.CascService
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EntitySubscriptionsCascContextIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var cascService: CascService

    // TODO Subscription for a validation
    // TODO Subscription for a branch
    // TODO Subscription for a project

    @Test
    fun `Subscription for a promotion`() {
        val target = uid("t")
        project {
            branch {
                val pl = promotionLevel()
                casc(
                    """
                        ontrack:
                            extensions:
                                notifications:
                                    entity-subscriptions:
                                        - entity:
                                            project: ${pl.project.name}
                                            branch: ${pl.branch.name}
                                            promotion: ${pl.name}
                                          subscriptions:
                                            - events:
                                                - new_promotion_run
                                              keywords: ""
                                              channel: mock
                                              channel-config:
                                                target: "$target"
                    """
                )
                // Checks that we can find this subscription
                asAdmin {
                    val subscriptions = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(
                            entity = pl.toProjectEntityID(),
                            channel = "mock",
                            channelConfig = target
                        )
                    )
                    assertEquals(1, subscriptions.pageItems.size)
                    val subscription = subscriptions.pageItems.first()
                    assertEquals(
                        setOf("new_promotion_run"),
                        subscription.data.events
                    )
                    assertEquals(
                        "",
                        subscription.data.keywords
                    )
                    assertEquals(
                        "mock",
                        subscription.data.channel
                    )
                    assertEquals(
                        mapOf("target" to target).asJson(),
                        subscription.data.channelConfig
                    )
                    assertEquals(
                        false,
                        subscription.data.disabled
                    )
                }
                // Checking the subscription
                build {
                    // Creating the event
                    promote(pl)
                    // Checking the notification
                    assertNotNull(mockNotificationChannel.messages[target], "Received notification on promotion") {
                        assertEquals(
                            "Build $name has been promoted to ${pl.name} for branch ${branch.name} in ${project.name}.",
                            it.first()
                        )
                    }
                }
            }
        }
    }

    /**
     * Runs a CasC from a series of YAML texts
     */
    private fun casc(vararg yaml: String) {
        asAdmin {
            cascService.runYaml(*yaml)
        }
    }

}
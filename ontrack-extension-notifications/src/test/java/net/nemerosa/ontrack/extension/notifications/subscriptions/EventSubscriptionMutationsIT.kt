package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredJsonField
import net.nemerosa.ontrack.json.getRequiredTextField
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventSubscriptionMutationsIT : AbstractNotificationTestSupport() {

    @Test
    fun `Creating a subscription`() {
        project {
            run("""
                mutation {
                    subscribeToEvents(input: {
                        projectEntity: {
                            type: PROJECT,
                            id: $id
                        },
                        channels: [
                            {
                                channel: "mock",
                                channelConfig: {
                                    target: "#test"
                                }
                            }
                        ],
                        events: [
                            "new_branch",
                            "delete_branch",
                            "new_promotion_run"
                        ]
                    }) {
                        errors {
                            message
                        }
                        subscription {
                            id
                        }
                    }
                }
            """) { data ->
                checkGraphQLUserErrors(data, "subscribeToEvents") { payload ->
                    val id = payload.getRequiredJsonField("subscription").getRequiredTextField("id")
                    val subscription: EventSubscription = eventSubscriptionService.getSubscriptionById(this, id)
                    assertEquals(
                        setOf(
                            "new_branch",
                            "delete_branch",
                            "new_promotion_run"
                        ),
                        subscription.events
                    )
                    assertEquals(this, subscription.projectEntity)
                    assertEquals(
                        setOf(
                            EventSubscriptionChannel(
                                "mock",
                                mapOf("target" to "#test").asJson()
                            )
                        ),
                        subscription.channels
                    )
                }
            }
        }
    }

}
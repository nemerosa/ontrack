package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredBooleanField
import net.nemerosa.ontrack.json.getRequiredJsonField
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.events.EventFactory
import org.junit.jupiter.api.Test
import kotlin.test.*

class EventSubscriptionMutationsIT : AbstractNotificationTestSupport() {

    @Test
    fun `Disabling and enabling a subscription for an entity`() {
        asAdmin {
            project {
                val record = eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig("#target"),
                    projectEntity = this,
                    keywords = null,
                    origin = "test",
                    EventFactory.NEW_PROMOTION_RUN,
                )
                // Disabling
                run("""
                    mutation {
                        disableSubscription(input: {
                            projectEntity: {
                                type: PROJECT,
                                id: $id
                            },
                            id: "${record.id}"
                        }) {
                            errors {
                                message
                            }
                            subscription {
                                id
                                disabled
                            }
                        }
                    }
                """) { data ->
                    checkGraphQLUserErrors(data, "disableSubscription") { payload ->
                        assertEquals(record.id, payload.path("subscription").getRequiredTextField("id"))
                        assertEquals(true, payload.path("subscription").getRequiredBooleanField("disabled"))
                        assertNotNull(eventSubscriptionService.findSubscriptionById(this, record.id)) {
                            assertTrue(it.disabled, "Subscription is disabled")
                        }
                    }
                }
                // Enabling
                run("""
                    mutation {
                        enableSubscription(input: {
                            projectEntity: {
                                type: PROJECT,
                                id: $id
                            },
                            id: "${record.id}"
                        }) {
                            errors {
                                message
                            }
                            subscription {
                                id
                                disabled
                            }
                        }
                    }
                """) { data ->
                    checkGraphQLUserErrors(data, "enableSubscription") { payload ->
                        assertEquals(record.id, payload.path("subscription").getRequiredTextField("id"))
                        assertEquals(false, payload.path("subscription").getRequiredBooleanField("disabled"))
                        assertNotNull(eventSubscriptionService.findSubscriptionById(this, record.id)) {
                            assertFalse(it.disabled, "Subscription is enabled")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Disabling and enabling a global subscription`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            val record = eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#target"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                EventFactory.NEW_PROMOTION_RUN,
            )
            // Disabling
            run("""
                    mutation {
                        disableSubscription(input: {
                            id: "${record.id}"
                        }) {
                            errors {
                                message
                            }
                            subscription {
                                id
                                disabled
                            }
                        }
                    }
                """) { data ->
                checkGraphQLUserErrors(data, "disableSubscription") { payload ->
                    assertEquals(record.id, payload.path("subscription").getRequiredTextField("id"))
                    assertEquals(true, payload.path("subscription").getRequiredBooleanField("disabled"))
                    assertNotNull(eventSubscriptionService.findSubscriptionById(null, record.id)) {
                        assertTrue(it.disabled, "Subscription is disabled")
                    }
                }
            }
            // Enabling
            run("""
                    mutation {
                        enableSubscription(input: {
                            id: "${record.id}"
                        }) {
                            errors {
                                message
                            }
                            subscription {
                                id
                                disabled
                            }
                        }
                    }
                """) { data ->
                checkGraphQLUserErrors(data, "enableSubscription") { payload ->
                    assertEquals(record.id, payload.path("subscription").getRequiredTextField("id"))
                    assertEquals(false, payload.path("subscription").getRequiredBooleanField("disabled"))
                    assertNotNull(eventSubscriptionService.findSubscriptionById(null, record.id)) {
                        assertFalse(it.disabled, "Subscription is enabled")
                    }
                }
            }
        }
    }

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
                        channel: "mock",
                        channelConfig: {
                            target: "#test"
                        },
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
                        "mock",
                        subscription.channel
                    )
                    assertEquals(
                        mapOf("target" to "#test").asJson(),
                        subscription.channelConfig
                    )
                }
            }
        }
    }

    @Test
    fun `Deleting a global subscription`() {
        asAdmin {
            val subscription = eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#main"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                EventFactory.NEW_PROMOTION_RUN,
            )
            run("""
                    mutation {
                        deleteSubscription(input:{id: "${subscription.id}"}) {
                            errors {
                                message
                            }
                        }
                    }
                """) { data ->
                checkGraphQLUserErrors(data, "deleteSubscription")
                assertNull(eventSubscriptionService.findSubscriptionById(null, subscription.id),
                    "Subscription has been deleted")
            }
        }
    }

    @Test
    fun `Deleting an entity subscription`() {
        asAdmin {
            project {
                val subscription = eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig("#main"),
                    projectEntity = this,
                    keywords = null,
                    origin = "test",
                    EventFactory.NEW_PROMOTION_RUN,
                )
                run("""
                    mutation {
                        deleteSubscription(input:{
                            id: "${subscription.id}",
                            projectEntity: {
                                type: PROJECT,
                                id: $id
                            }
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """) { data ->
                    checkGraphQLUserErrors(data, "deleteSubscription")
                    assertNull(eventSubscriptionService.findSubscriptionById(this, subscription.id),
                        "Subscription has been deleted")
                }
            }
        }
    }

}
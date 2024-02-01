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
    fun `Settings subscriptions using the branch mutation`() {
        asAdmin {
            project {
                branch {
                    run(
                        """
                        mutation {
                            subscribeBranchToEvents(input: {
                                project: "${project.name}",
                                branch: "$name",
                                channel: "mock",
                                channelConfig: {
                                    target: "#test"
                                },
                                events: [
                                    "new_promotion_run"
                                ],
                                keywords: "GOLD",
                            }) {
                                errors {
                                    message
                                }
                                subscription {
                                    id
                                }
                            }
                        }
                    """
                    ) { data ->
                        checkGraphQLUserErrors(data, "subscribeBranchToEvents") { payload ->
                            val id = payload.getRequiredJsonField("subscription").getRequiredTextField("id")
                            val subscription: EventSubscription = eventSubscriptionService.getSubscriptionById(this, id)
                            assertEquals(
                                setOf(
                                    "new_promotion_run"
                                ),
                                subscription.events
                            )
                            assertEquals(this, subscription.projectEntity)
                            assertEquals("GOLD", subscription.keywords)
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
        }
    }

    @Test
    fun `Settings subscriptions using the promotion level mutation`() {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel("GOLD")
                    run(
                        """
                        mutation {
                            subscribePromotionLevelToEvents(input: {
                                project: "${project.name}",
                                branch: "$name",
                                promotion: "GOLD",
                                channel: "mock",
                                channelConfig: {
                                    target: "#test"
                                },
                                events: [
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
                    """
                    ) { data ->
                        checkGraphQLUserErrors(data, "subscribePromotionLevelToEvents") { payload ->
                            val id = payload.getRequiredJsonField("subscription").getRequiredTextField("id")
                            val subscription: EventSubscription = eventSubscriptionService.getSubscriptionById(pl, id)
                            assertEquals(
                                setOf(
                                    "new_promotion_run"
                                ),
                                subscription.events
                            )
                            assertEquals(pl, subscription.projectEntity)
                            assertEquals(null, subscription.keywords)
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
        }
    }

    @Test
    fun `Settings subscriptions using the promotion level mutation with a custom content template`() {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel("GOLD")
                    run(
                        """
                        mutation SetupPromotionSubscription(
                            ${'$'}contentTemplate: String!,
                        ) {
                            subscribePromotionLevelToEvents(input: {
                                project: "${project.name}",
                                branch: "$name",
                                promotion: "GOLD",
                                channel: "mock",
                                channelConfig: {
                                    target: "#test"
                                },
                                events: [
                                    "new_promotion_run"
                                ],
                                contentTemplate: ${'$'}contentTemplate,
                            }) {
                                errors {
                                    message
                                }
                                subscription {
                                    id
                                }
                            }
                        }
                    """,
                        variables = mapOf(
                            "contentTemplate" to "Change log for this promotion!"
                        )
                    ) { data ->
                        checkGraphQLUserErrors(data, "subscribePromotionLevelToEvents") { payload ->
                            val id = payload.getRequiredJsonField("subscription").getRequiredTextField("id")
                            val subscription: EventSubscription = eventSubscriptionService.getSubscriptionById(pl, id)
                            assertEquals(
                                setOf(
                                    "new_promotion_run"
                                ),
                                subscription.events
                            )
                            assertEquals(pl, subscription.projectEntity)
                            assertEquals(null, subscription.keywords)
                            assertEquals(
                                "mock",
                                subscription.channel
                            )
                            assertEquals(
                                mapOf("target" to "#test").asJson(),
                                subscription.channelConfig
                            )
                            assertEquals(
                                "Change log for this promotion!",
                                subscription.contentTemplate,
                            )
                        }
                    }
                }
            }
        }
    }

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
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN,
                )
                // Disabling
                run(
                    """
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
                """
                ) { data ->
                    checkGraphQLUserErrors(data, "disableSubscription") { payload ->
                        assertEquals(record.id, payload.path("subscription").getRequiredTextField("id"))
                        assertEquals(true, payload.path("subscription").getRequiredBooleanField("disabled"))
                        assertNotNull(eventSubscriptionService.findSubscriptionById(this, record.id)) {
                            assertTrue(it.disabled, "Subscription is disabled")
                        }
                    }
                }
                // Enabling
                run(
                    """
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
                """
                ) { data ->
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
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN,
            )
            // Disabling
            run(
                """
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
                """
            ) { data ->
                checkGraphQLUserErrors(data, "disableSubscription") { payload ->
                    assertEquals(record.id, payload.path("subscription").getRequiredTextField("id"))
                    assertEquals(true, payload.path("subscription").getRequiredBooleanField("disabled"))
                    assertNotNull(eventSubscriptionService.findSubscriptionById(null, record.id)) {
                        assertTrue(it.disabled, "Subscription is disabled")
                    }
                }
            }
            // Enabling
            run(
                """
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
                """
            ) { data ->
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
    fun `Creating a global subscription without a template`() {
        project {
            run(
                """
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
                        ],
                    }) {
                        errors {
                            message
                        }
                        subscription {
                            id
                        }
                    }
                }
            """
            ) { data ->
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
                    assertEquals(
                        null,
                        subscription.contentTemplate,
                    )
                }
            }
        }
    }

    @Test
    fun `Creating a global subscription with a template`() {
        project {
            run(
                """
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
                        ],
                        contentTemplate: "This is a poor template",
                    }) {
                        errors {
                            message
                        }
                        subscription {
                            id
                        }
                    }
                }
            """
            ) { data ->
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
                    assertEquals(
                        "This is a poor template",
                        subscription.contentTemplate,
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
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN,
            )
            run(
                """
                    mutation {
                        deleteSubscription(input:{id: "${subscription.id}"}) {
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "deleteSubscription")
                assertNull(
                    eventSubscriptionService.findSubscriptionById(null, subscription.id),
                    "Subscription has been deleted"
                )
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
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN,
                )
                run(
                    """
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
                """
                ) { data ->
                    checkGraphQLUserErrors(data, "deleteSubscription")
                    assertNull(
                        eventSubscriptionService.findSubscriptionById(this, subscription.id),
                        "Subscription has been deleted"
                    )
                }
            }
        }
    }

}
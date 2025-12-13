package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredBooleanField
import net.nemerosa.ontrack.json.getRequiredJsonField
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@AsAdminTest
internal class GQLRootQueryEventSubscriptionsIT : AbstractNotificationTestSupport() {

    @Test
    fun `Filtering entity subscriptions using its name`() {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    val name = uid("sub-")
                    eventSubscriptionService.subscribe(
                        name = name,
                        channel = mockNotificationChannel,
                        channelConfig = MockNotificationChannelConfig("#one"),
                        projectEntity = pl,
                        keywords = null,
                        origin = "test",
                        contentTemplate = null,
                        EventFactory.NEW_PROMOTION_RUN
                    )

                    // Query
                    run(
                        """
                            query {
                                eventSubscriptions(filter: {
                                    entity: {
                                        type: PROMOTION_LEVEL,
                                        id: ${pl.id}
                                    },
                                    name: "$name"
                                }) {
                                    pageItems {
                                        name
                                    }
                                }
                            }
                        """
                    ) { data ->
                        val sub = data.path("eventSubscriptions")
                            .path("pageItems").firstOrNull()
                        assertNotNull(sub, "Subscription found") {
                            assertEquals(name, it.getRequiredTextField("name"))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Filtering global subscriptions using its name`() {
        asAdmin {
            val name = uid("sub-")
            eventSubscriptionService.subscribe(
                name = name,
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )

            // Query
            run(
                """
                    query {
                        eventSubscriptions(filter: {
                            name: "$name"
                        }) {
                            pageItems {
                                name
                            }
                        }
                    }
                """
            ) { data ->
                val sub = data.path("eventSubscriptions")
                    .path("pageItems").firstOrNull()
                assertNotNull(sub, "Subscription found") {
                    assertEquals(name, it.getRequiredTextField("name"))
                }
            }
        }
    }

    @Test
    fun `Filtering the project subscriptions and getting the access rights`() {
        project {
            // Subscribe for events on this project for the two different event types
            eventSubscriptionService.subscribe(
                name = "test",
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            // Query
            asUser().withView(this).withProjectFunction(this, ProjectSubscriptionsRead::class.java).call {
                run(
                    """
                    query {
                        eventSubscriptions(filter: {
                            entity: {
                                type: PROJECT,
                                id: $id
                            }
                        }) {
                            writeSubscriptionGranted
                        }
                    }
                """
                ) { data ->
                    assertFalse(
                        data.getRequiredJsonField("eventSubscriptions")
                            .getRequiredBooleanField("writeSubscriptionGranted")
                    )
                }
            }
            // Query
            asUser().withView(this).withProjectFunction(this, ProjectSubscriptionsRead::class.java)
                .withProjectFunction(this, ProjectSubscriptionsWrite::class.java)
                .call {
                    run(
                        """
                    query {
                        eventSubscriptions(filter: {
                            entity: {
                                type: PROJECT,
                                id: $id
                            }
                        }) {
                            writeSubscriptionGranted
                        }
                    }
                """
                    ) { data ->
                        assertTrue(
                            data.getRequiredJsonField("eventSubscriptions")
                                .getRequiredBooleanField("writeSubscriptionGranted")
                        )
                    }
                }
        }
    }

    @Test
    fun `Filtering the subscriptions for an entity using event type`() {
        project {
            // Subscribe for events on this project for the two different event types
            eventSubscriptionService.subscribe(
                name = uid("p"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                name = uid("p"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#two"),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_VALIDATION_RUN
            )
            // Query
            run(
                """
                    query {
                        eventSubscriptions(filter: {
                            entity: {
                                type: PROJECT,
                                id: $id
                            },
                            eventType: "new_promotion_run"
                        }) {
                            pageItems {
                                channel
                                channelConfig
                                events
                            }
                        }
                    }
            """
            ) { data ->
                assertEquals(
                    mapOf(
                        "eventSubscriptions" to mapOf(
                            "pageItems" to listOf(
                                mapOf(
                                    "channel" to "mock",
                                    "channelConfig" to mapOf(
                                        "target" to "#one",
                                        "data" to null,
                                        "rendererType" to null,
                                    ),
                                    "events" to listOf(
                                        "new_promotion_run"
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Getting the subscriptions for an entity with a content template`() {
        project {
            eventSubscriptionService.subscribe(
                name = uid("p"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = "Some template.",
                EventFactory.NEW_PROMOTION_RUN
            )
            // Query
            run(
                """
                    query {
                        eventSubscriptions(filter: {
                            entity: {
                                type: PROJECT,
                                id: $id
                            }
                        }) {
                            pageItems {
                                contentTemplate
                            }
                        }
                    }
            """
            ) { data ->
                assertEquals(
                    mapOf(
                        "eventSubscriptions" to mapOf(
                            "pageItems" to listOf(
                                mapOf(
                                    "contentTemplate" to "Some template.",
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Getting the first subscriptions for an entity`() {
        project {
            // Creating 10 subscriptions
            (1..10).forEach {
                eventSubscriptionService.subscribe(
                    name = uid("p"),
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig("#channel-$it"),
                    projectEntity = this,
                    keywords = null,
                    origin = "test",
                    contentTemplate = "Subscription #$it",
                    EventFactory.NEW_PROMOTION_RUN
                )
            }
            // Query
            run(
                """
                    query {
                        eventSubscriptions(
                            size: 2,
                            filter: {
                                entity: {
                                    type: PROJECT,
                                    id: $id
                                }
                            }
                        ) {
                            pageInfo {
                                totalSize
                            }
                            pageItems {
                                contentTemplate
                            }
                        }
                    }
            """
            ) { data ->
                val eventSubscriptions = data.path("eventSubscriptions")
                assertEquals(10, eventSubscriptions.path("pageInfo").path("totalSize").asInt())
                val items = eventSubscriptions.path("pageItems")
                assertEquals(2, items.size())
            }
        }
    }

    @Test
    fun `Getting a global subscription with a content template`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            eventSubscriptionService.subscribe(
                name = uid("p"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = "This is my template.",
                EventFactory.NEW_PROMOTION_RUN
            )
            run(
                """
                    query {
                        eventSubscriptions {
                            pageItems {
                                contentTemplate
                            }
                        }
                    }
            """
            ) { data ->
                assertEquals(
                    mapOf(
                        "eventSubscriptions" to mapOf(
                            "pageItems" to listOf(
                                mapOf(
                                    "contentTemplate" to "This is my template.",
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Filtering the global subscriptions using event type`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            // Subscribe for events on this project for the two different event types
            eventSubscriptionService.subscribe(
                name = uid("p"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                name = uid("p"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#two"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_VALIDATION_RUN
            )
            // Query
            run(
                """
                    query {
                        eventSubscriptions(filter: {
                            eventType: "new_promotion_run"
                        }) {
                            pageItems {
                                channel
                                channelConfig
                                events
                            }
                        }
                    }
            """
            ) { data ->
                assertEquals(
                    mapOf(
                        "eventSubscriptions" to mapOf(
                            "pageItems" to listOf(
                                mapOf(
                                    "channel" to "mock",
                                    "channelConfig" to mapOf(
                                        "target" to "#one",
                                        "data" to null,
                                        "rendererType" to null,
                                    ),
                                    "events" to listOf(
                                        "new_promotion_run"
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }


}
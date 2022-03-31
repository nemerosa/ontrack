package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredBooleanField
import net.nemerosa.ontrack.json.getRequiredJsonField
import net.nemerosa.ontrack.model.events.EventFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class GQLRootQueryEventSubscriptionsIT : AbstractNotificationTestSupport() {

    @Test
    fun `Filtering the project subscriptions and getting the access rights`() {
        project {
            // Subscribe for events on this project for the two different event types
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = this,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            // Query
            asUser().with(this, ProjectSubscriptionsRead::class.java).call {
                run("""
                    query {
                        eventSubscriptions(filter: {
                            entity: {
                                type: PROJECT,
                                id: $id
                            }
                        }) {
                            createSubscriptionGranted
                        }
                    }
                """) { data ->
                    assertFalse(data.getRequiredJsonField("eventSubscriptions")
                        .getRequiredBooleanField("createSubscriptionGranted"))
                }
            }
            // Query
            asUser().with(this, ProjectSubscriptionsRead::class.java).with(this, ProjectSubscriptionsWrite::class.java)
                .call {
                    run("""
                    query {
                        eventSubscriptions(filter: {
                            entity: {
                                type: PROJECT,
                                id: $id
                            }
                        }) {
                            createSubscriptionGranted
                        }
                    }
                """) { data ->
                        assertTrue(data.getRequiredJsonField("eventSubscriptions")
                            .getRequiredBooleanField("createSubscriptionGranted"))
                    }
                }
        }
    }

    @Test
    fun `Filtering the subscriptions for an entity using event type`() {
        project {
            // Subscribe for events on this project for the two different event types
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = this,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#two"),
                projectEntity = this,
                keywords = null,
                EventFactory.NEW_VALIDATION_RUN
            )
            // Query
            run("""
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
            """) { data ->
                assertEquals(
                    mapOf(
                        "eventSubscriptions" to mapOf(
                            "pageItems" to listOf(
                                mapOf(
                                    "channel" to "mock",
                                    "channelConfig" to mapOf(
                                        "target" to "#one"
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
    fun `Filtering the global subscriptions using event type`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            // Subscribe for events on this project for the two different event types
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#two"),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_VALIDATION_RUN
            )
            // Query
            run("""
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
            """) { data ->
                assertEquals(
                    mapOf(
                        "eventSubscriptions" to mapOf(
                            "pageItems" to listOf(
                                mapOf(
                                    "channel" to "mock",
                                    "channelConfig" to mapOf(
                                        "target" to "#one"
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
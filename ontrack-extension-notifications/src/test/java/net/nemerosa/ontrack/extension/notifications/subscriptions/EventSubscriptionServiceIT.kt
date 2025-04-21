package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@AsAdminTest
internal class EventSubscriptionServiceIT : AbstractNotificationTestSupport() {

    @Test
    fun `Getting the global subscriptions for an event`() {
        val name = uid("g")
        val targetGlobal = uid("t")
        val targetProject = uid("t")
        asAdmin {
            // Register globally
            eventSubscriptionService.subscribe(
                name = name,
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(targetGlobal),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_BRANCH,
            )
            // Project level
            val project = project {
                eventSubscriptionService.subscribe(
                    name = "test",
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetProject),
                    projectEntity = this,
                    keywords = null,
                    origin = "test",
                    contentTemplate = null,
                    EventFactory.NEW_BRANCH,
                )
            }
            // Creating a branch
            val branch = project.branch()
            // Checks that notifications have been received both at project & global level
            assertEquals(
                listOf(
                    "New branch ${branch.name} for project ${project.name}."
                ),
                mockNotificationChannel.messages[targetGlobal]?.toList()
            )
            assertEquals(
                listOf(
                    "New branch ${branch.name} for project ${project.name}."
                ),
                mockNotificationChannel.messages[targetProject]?.toList()
            )
        }
    }

    @Test
    fun `Getting the subscriptions for an event`() {
        val targetBranch = uid("tb")
        val targetProject = uid("tp")
        project {
            branch {
                // Subscription at branch level
                eventSubscriptionService.subscribe(
                    name = "test",
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetBranch),
                    projectEntity = this, // Branch
                    keywords = null,
                    origin = "test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
                // Subscription at project level
                eventSubscriptionService.subscribe(
                    name = "test",
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetProject),
                    projectEntity = project,
                    keywords = null,
                    origin = "test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
                // Gettings the subscriptions for this project & the new promotion run event
                // (without actually testing the notification)
                val build = Build.of(this, NameDescription.nd("1", ""), Signature.of("test")).withId(ID.of(10))
                val promotionLevel = PromotionLevel.of(this, NameDescription.nd("PL", "")).withId(ID.of(100))
                val subscriptions = mutableListOf<EventSubscription>()
                eventSubscriptionService.forEveryMatchingSubscription(
                    eventFactory.newPromotionRun(
                        PromotionRun.of(
                            build,
                            promotionLevel,
                            Signature.of("test"),
                            null
                        ).withId(ID.of(1000))
                    )
                ) { subscriptions += it }
                // Checks the subscriptions
                // We expect one for the branch and one for the project
                // Sorting per target
                subscriptions.sortBy { it.channelConfig["target"].asText() }
                assertEquals(2, subscriptions.size)
                // Branch first (tb)
                subscriptions[0].let { subscription ->
                    assertEquals(this, subscription.projectEntity)
                    assertEquals(
                        "mock",
                        subscription.channel
                    )
                    assertEquals(
                        mapOf(
                            "target" to targetBranch,
                            "data" to null,
                            "rendererType" to null,
                        ).asJson(),
                        subscription.channelConfig
                    )
                    assertEquals(setOf("new_promotion_run"), subscription.events)
                }
                // Project second (tp)
                subscriptions[1].let { subscription ->
                    assertEquals(project, subscription.projectEntity)
                    assertEquals(
                        "mock",
                        subscription.channel
                    )
                    assertEquals(
                        mapOf(
                            "target" to targetProject,
                            "data" to null,
                            "rendererType" to null,
                        ).asJson(),
                        subscription.channelConfig
                    )
                    assertEquals(setOf("new_promotion_run"), subscription.events)
                }
            }
        }
    }

    @Test
    fun `Getting the subscriptions for an event based on an event filter`() {
        val target = uid("t")
        project {
            branch {
                val silver = promotionLevel("SILVER")
                val gold = promotionLevel("GOLD")
                // Registering a subscription for new promotion runs at project level
                // but only for GOLD promotions
                eventSubscriptionService.subscribe(
                    name = "test",
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(target),
                    projectEntity = project,
                    keywords = "GOLD",
                    origin = "test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN,
                )
                // Build to promote
                build {
                    // Creates a SILVER promotion, checks it's not notified
                    promote(silver)
                    assertNull(
                        mockNotificationChannel.messages[target],
                        "No notification received for the the silver promotion"
                    )
                    // Creates a GOLD promotion, checks it's notified
                    promote(gold)
                    assertNotNull(mockNotificationChannel.messages[target]) {
                        assertEquals(
                            "Build $name has been promoted to GOLD for branch ${branch.name} in ${project.name}.",
                            it.first()
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Getting the subscriptions for an event based on an event filter on several tokens`() {
        val target = uid("t")
        project {
            val main = branch("main")
            val other = branch("other")
            // Registering a subscription for new promotion runs at project level
            // but only for GOLD promotions on the main branch
            eventSubscriptionService.subscribe(
                name = "test",
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(target),
                projectEntity = this,
                keywords = "GOLD main",
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN,
            )
            // Promotion on the other branch ==> no notification
            val otherPromotion = other.promotionLevel("GOLD")
            other.build {
                promote(otherPromotion)
                assertNull(
                    mockNotificationChannel.messages[target],
                    "No notification received for the the gold promotion on the other branch"
                )
            }
            // Promotion on the main branch ==> notification
            val mainPromotion = main.promotionLevel("GOLD")
            main.build {
                promote(mainPromotion)
                assertNotNull(mockNotificationChannel.messages[target]) {
                    assertEquals(
                        "Build $name has been promoted to GOLD for branch ${branch.name} in ${project.name}.",
                        it.first()
                    )
                }
            }
        }
    }

    @Test
    fun `Saving a global subscription`() {
        asAdmin {
            val name = uid("g")
            eventSubscriptionService.subscribe(
                name = name,
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#target"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            assertNotNull(eventSubscriptionService.findSubscriptionByName(null, name)) {
                assertEquals(
                    EventSubscription(
                        name = name,
                        channel = mockNotificationChannel.type,
                        channelConfig = MockNotificationChannelConfig("#target").asJson(),
                        projectEntity = null,
                        keywords = null,
                        origin = "test",
                        contentTemplate = null,
                        disabled = false,
                        events = setOf(EventFactory.NEW_PROMOTION_RUN.id),
                    ),
                    it
                )
            }
        }
    }

    @Test
    fun `Filtering the subscriptions for an entity with recursivity`() {
        val targetPromotionLevel = uid("PL")
        val targetBranch = uid("B")
        val targetProject = uid("P")
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
        }
        project {
            // Subscribe for events on this project
            eventSubscriptionService.subscribe(
                name = "test",
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(targetProject),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            branch {
                // Subscribe for events on this branch
                eventSubscriptionService.subscribe(
                    name = "test",
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetBranch),
                    projectEntity = this,
                    keywords = null,
                    origin = "test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
                promotionLevel {
                    // Subscribe for events on this promotion
                    eventSubscriptionService.subscribe(
                        name = "test",
                        channel = mockNotificationChannel,
                        channelConfig = MockNotificationChannelConfig(targetPromotionLevel),
                        projectEntity = this,
                        keywords = null,
                        origin = "test",
                        contentTemplate = null,
                        EventFactory.NEW_PROMOTION_RUN
                    )
                    // Looking for all subscriptions on this promotion, and recursively
                    val page = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(entity = toProjectEntityID(), recursive = true)
                    )
                    assertEquals(3, page.pageInfo.totalSize)
                    assertEquals(3, page.pageItems.size)
                    val targets =
                        page.pageItems.map { it.channelConfig.getRequiredTextField("target") }
                    assertEquals(
                        setOf(
                            targetProject,
                            targetBranch,
                            targetPromotionLevel,
                        ),
                        targets.toSet()
                    )
                }
            }
        }
    }

    @Test
    fun `Getting the global subscriptions`() {
        asAdmin {
            val name = uid("g")
            val target = uid("t")
            eventSubscriptionService.subscribe(
                name = name,
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(target),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            val subscriptions = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(size = 1000)
            ).pageItems
            assertNotNull(
                subscriptions.find { it.channelConfig.getTextField("target") == target },
                "Finding the global subscription"
            )
        }
    }

    @Test
    fun `Getting the global subscriptions with recursivity`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            val name = uid("g")
            val target = uid("t")
            eventSubscriptionService.subscribe(
                name = name,
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(target),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            val project = project {
                eventSubscriptionService.subscribe(
                    name = "test",
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(target),
                    projectEntity = this,
                    keywords = null,
                    origin = "test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
            }
            val subscriptions = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(size = 1000, entity = project.toProjectEntityID(), recursive = true)
            ).pageItems
            assertEquals(2, subscriptions.size)
            assertEquals(project, subscriptions[0].projectEntity)
            assertNull(subscriptions[1].projectEntity)
        }
    }

    @Test
    fun `Filtering the global subscriptions using a channel`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            eventSubscriptionService.subscribe(
                name = uid("g"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#mock"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                name = uid("g"),
                channel = otherMockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#other"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(channel = "other-mock")
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            assertEquals(
                "other-mock",
                page.pageItems.first().channel
            )
        }
    }

    @Test
    fun `Filtering the global subscriptions using a channel config`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            eventSubscriptionService.subscribe(
                name = uid("g"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#main"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                name = uid("g"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#test"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(channel = "mock", channelConfig = "#main")
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            assertEquals(
                "mock",
                page.pageItems.first().channel
            )
            assertEquals(
                "#main",
                page.pageItems.first().channelConfig.getRequiredTextField("target")
            )
        }
    }

    @Test
    fun `Filtering the global subscriptions using event type`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            eventSubscriptionService.subscribe(
                name = uid("g"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                name = uid("g"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#two"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_VALIDATION_RUN
            )
            //
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(eventType = "new_promotion_run")
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            val subscription = page.pageItems.first()
            assertEquals(
                "mock",
                subscription.channel
            )
            assertEquals(
                "#one",
                subscription.channelConfig.getRequiredTextField("target")
            )
            assertEquals(
                setOf("new_promotion_run"),
                subscription.events
            )
        }
    }

    @Test
    fun `Filtering the subscriptions for an entity without recursivity`() {
        val targetPromotionLevel = uid("PL")
        val targetBranch = uid("B")
        val targetProject = uid("P")
        project {
            // Subscribe for events on this project
            eventSubscriptionService.subscribe(
                name = uid("p"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(targetProject),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            branch {
                // Subscribe for events on this branch
                eventSubscriptionService.subscribe(
                    name = uid("b"),
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetBranch),
                    projectEntity = this,
                    keywords = null,
                    origin = "test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
                promotionLevel {
                    // Subscribe for events on this promotion
                    eventSubscriptionService.subscribe(
                        name = uid("pl"),
                        channel = mockNotificationChannel,
                        channelConfig = MockNotificationChannelConfig(targetPromotionLevel),
                        projectEntity = this,
                        keywords = null,
                        origin = "test",
                        contentTemplate = null,
                        EventFactory.NEW_PROMOTION_RUN
                    )
                    // Looking for all subscriptions on this promotion, and recursively
                    val page = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(entity = toProjectEntityID(), recursive = false)
                    )
                    assertEquals(1, page.pageInfo.totalSize)
                    assertEquals(1, page.pageItems.size)
                    val targets =
                        page.pageItems.map { it.channelConfig.getRequiredTextField("target") }
                    assertEquals(
                        setOf(
                            targetPromotionLevel,
                        ),
                        targets.toSet()
                    )
                }
            }
        }
    }

    @Test
    fun `Filtering the subscriptions for an entity using a channel`() {
        project {
            // Subscribe for events on this project for the two different channels
            eventSubscriptionService.subscribe(
                name = uid("p"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#mock"),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                name = uid("p"),
                channel = otherMockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#other"),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            // Looking for all subscriptions on this promotion, and recursively
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(entity = toProjectEntityID(), channel = "other-mock")
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            assertEquals(
                "other-mock",
                page.pageItems.first().channel
            )
        }
    }

    @Test
    fun `Filtering the subscriptions for an entity using a channel config`() {
        project {
            // Subscribe for events on this project for the two different channels configurations
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
                EventFactory.NEW_PROMOTION_RUN
            )
            // Looking for all subscriptions on this promotion, and recursively
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(entity = toProjectEntityID(), channel = "mock", channelConfig = "#one")
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            assertEquals(
                "mock",
                page.pageItems.first().channel
            )
            assertEquals(
                "#one",
                page.pageItems.first().channelConfig.getRequiredTextField("target")
            )
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
            //
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(entity = toProjectEntityID(), eventType = "new_promotion_run")
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            val subscription = page.pageItems.first()
            assertEquals(
                "mock",
                subscription.channel
            )
            assertEquals(
                "#one",
                subscription.channelConfig.getRequiredTextField("target")
            )
            assertEquals(
                setOf("new_promotion_run"),
                subscription.events
            )
        }
    }

    @Test
    fun `Content template is not part of what identifies a subscription`() {
        project {
            // Initial subscription with a first template
            eventSubscriptionService.subscribe(
                name = "test",
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = "This is the first template",
                EventFactory.NEW_PROMOTION_RUN
            )
            // Second subscription with a different template
            eventSubscriptionService.subscribe(
                name = "test",
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = "This is the second template",
                EventFactory.NEW_PROMOTION_RUN
            )
            // Getting the list of subscriptions for this entity
            val subscriptions = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(entity = toProjectEntityID())
            ).pageItems
            // We expect only the second subscription to have been kept
            assertEquals(1, subscriptions.size, "Only one subscription must be kept")
            val subscription = subscriptions.first()
            assertEquals(
                "This is the second template",
                subscription.contentTemplate
            )
        }
    }

    @Test
    fun `Content template is not part of what identifies a global subscription`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            // Initial subscription with a first template
            eventSubscriptionService.subscribe(
                name = "test",
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = "This is the first template",
                EventFactory.NEW_PROMOTION_RUN
            )
            // Second subscription with a different template
            eventSubscriptionService.subscribe(
                name = "test",
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = null,
                keywords = null,
                origin = "test",
                contentTemplate = "This is the second template",
                EventFactory.NEW_PROMOTION_RUN
            )
            // Getting the list of global subscriptions
            val subscriptions = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter()
            ).pageItems
            // We expect only the second subscription to have been kept
            assertEquals(1, subscriptions.size, "Only one subscription must be kept")
            val subscription = subscriptions.first()
            assertEquals(
                "This is the second template",
                subscription.contentTemplate
            )
        }
    }

    @Test
    fun `Saving a global subscription checks the validity of its configuration`() {
        asAdmin {
            val name = uid("g")
            assertFailsWith<EventSubscriptionConfigException> {
                eventSubscriptionService.subscribe(
                    name = name,
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(""),
                    projectEntity = null,
                    keywords = null,
                    origin = "test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
            }
        }
    }

    @Test
    fun `Saving an entity subscription checks the validity of its configuration`() {
        asAdmin {
            project {
                assertFailsWith<EventSubscriptionConfigException> {
                    eventSubscriptionService.subscribe(
                        name = "test",
                        channel = mockNotificationChannel,
                        channelConfig = MockNotificationChannelConfig(""),
                        projectEntity = this,
                        keywords = null,
                        origin = "test",
                        contentTemplate = null,
                        EventFactory.NEW_PROMOTION_RUN
                    )
                }
            }
        }
    }

}
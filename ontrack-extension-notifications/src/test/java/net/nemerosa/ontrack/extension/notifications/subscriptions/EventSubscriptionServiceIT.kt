package net.nemerosa.ontrack.extension.notifications.subscriptions

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class EventSubscriptionServiceIT : AbstractNotificationTestSupport() {

    @Test
    fun `Getting the global subscriptions for an event`() {
        val targetGlobal = uid("t")
        val targetProject = uid("t")
        asAdmin {
            // Register globally
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(targetGlobal),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_BRANCH,
            )
            // Project level
            val project = project {
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetProject),
                    projectEntity = this,
                    keywords = null,
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
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetBranch),
                    projectEntity = this, // Branch
                    keywords = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
                // Subscription at project level
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetProject),
                    projectEntity = project,
                    keywords = null,
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
                subscriptions.sortBy { it.channels.first().channelConfig["target"].asText() }
                assertEquals(2, subscriptions.size)
                // Branch first (tb)
                subscriptions[0].let { subscription ->
                    assertEquals(this, subscription.projectEntity)
                    assertEquals(setOf(
                        EventSubscriptionChannel(
                            channel = "mock",
                            channelConfig = mapOf("target" to targetBranch).asJson()
                        )
                    ), subscription.channels)
                    assertEquals(setOf("new_promotion_run"), subscription.events)
                }
                // Project second (tp)
                subscriptions[1].let { subscription ->
                    assertEquals(project, subscription.projectEntity)
                    assertEquals(setOf(
                        EventSubscriptionChannel(
                            channel = "mock",
                            channelConfig = mapOf("target" to targetProject).asJson()
                        )
                    ), subscription.channels)
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
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(target),
                    projectEntity = project,
                    keywords = "GOLD",
                    EventFactory.NEW_PROMOTION_RUN,
                )
                // Build to promote
                build {
                    // Creates a SILVER promotion, checks it's not notified
                    promote(silver)
                    assertNull(mockNotificationChannel.messages[target],
                        "No notification received for the the silver promotion")
                    // Creates a GOLD promotion, checks it's notified
                    promote(gold)
                    assertNotNull(mockNotificationChannel.messages[target]) {
                        assertEquals("Build $name has been promoted to GOLD for branch ${branch.name} in ${project.name}.",
                            it.first())
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
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(target),
                projectEntity = this,
                keywords = "GOLD main",
                EventFactory.NEW_PROMOTION_RUN,
            )
            // Promotion on the other branch ==> no notification
            val otherPromotion = other.promotionLevel("GOLD")
            other.build {
                promote(otherPromotion)
                assertNull(mockNotificationChannel.messages[target],
                    "No notification received for the the gold promotion on the other branch")
            }
            // Promotion on the main branch ==> notification
            val mainPromotion = main.promotionLevel("GOLD")
            main.build {
                promote(mainPromotion)
                assertNotNull(mockNotificationChannel.messages[target]) {
                    assertEquals("Build $name has been promoted to GOLD for branch ${branch.name} in ${project.name}.",
                        it.first())
                }
            }
        }
    }

    @Test
    fun `Saving a global subscription`() {
        asAdmin {
            val record = eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#target"),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            assertNotNull(eventSubscriptionService.findSubscriptionById(null, record.id)) {
                assertEquals(record.data, it)
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
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(targetProject),
                projectEntity = this,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            branch {
                // Subscribe for events on this branch
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetBranch),
                    projectEntity = this,
                    keywords = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
                promotionLevel {
                    // Subscribe for events on this promotion
                    eventSubscriptionService.subscribe(
                        channel = mockNotificationChannel,
                        channelConfig = MockNotificationChannelConfig(targetPromotionLevel),
                        projectEntity = this,
                        keywords = null,
                        EventFactory.NEW_PROMOTION_RUN
                    )
                    // Looking for all subscriptions on this promotion, and recursively
                    val page = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(entity = toProjectEntityID(), recursive = true)
                    )
                    assertEquals(3, page.pageInfo.totalSize)
                    assertEquals(3, page.pageItems.size)
                    val targets =
                        page.pageItems.map { it.data.channels.first().channelConfig.getRequiredTextField("target") }
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
            val target = uid("t")
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(target),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            val subscriptions = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(size = 1000)
            ).pageItems
            assertNotNull(subscriptions.find { it.data.channels.firstOrNull()?.channelConfig?.getTextField("target") == target },
                "Finding the global subscription")
        }
    }

    @Test
    fun `Getting the global subscriptions with recursivity`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            val target = uid("t")
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(target),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            val project = project {
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(target),
                    projectEntity = this,
                    keywords = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
            }
            val subscriptions = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(size = 1000, entity = project.toProjectEntityID(), recursive = true)
            ).pageItems
            assertEquals(2, subscriptions.size)
            assertEquals(project, subscriptions[0].data.projectEntity)
            assertNull(subscriptions[1].data.projectEntity)
        }
    }

    @Test
    fun `Filtering the global subscriptions using a channel`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#mock"),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                channel = otherMockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#other"),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(channel = "other-mock")
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            assertEquals(
                "other-mock",
                page.pageItems.first().data.channels.first().channel
            )
        }
    }

    @Test
    fun `Filtering the global subscriptions using a channel config`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#main"),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#test"),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(channel = "mock", channelConfig = "#main")
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            assertEquals(
                "mock",
                page.pageItems.first().data.channels.first().channel
            )
            assertEquals(
                "#main",
                page.pageItems.first().data.channels.first().channelConfig.getRequiredTextField("target")
            )
        }
    }

    @Test
    fun `Filtering the global subscriptions using event type`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
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
            //
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(eventType = "new_promotion_run")
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            val subscription = page.pageItems.first()
            val channel = subscription.data.channels.first()
            assertEquals(
                "mock",
                channel.channel
            )
            assertEquals(
                "#one",
                channel.channelConfig.getRequiredTextField("target")
            )
            assertEquals(
                setOf("new_promotion_run"),
                subscription.data.events
            )
        }
    }

    @Test
    fun `Filtering the global subscriptions using creation date`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            runBlocking {
                delay(2_000L)
            }
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#two"),
                projectEntity = null,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(createdBefore = Time.now().minusSeconds(1))
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            assertEquals(
                "mock",
                page.pageItems.first().data.channels.first().channel
            )
            assertEquals(
                "#one",
                page.pageItems.first().data.channels.first().channelConfig.getRequiredTextField("target")
            )
        }
    }

    @Test
    fun `Filtering the global subscriptions using creator`() {
        asAdmin {
            eventSubscriptionService.removeAllGlobal()
            val user1 = asUser().with(GlobalSubscriptionsManage::class.java).call {
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig("#one"),
                    projectEntity = null,
                    keywords = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
                securityService.currentSignature
            }
            asUser().with(GlobalSubscriptionsManage::class.java).call {
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig("#two"),
                    projectEntity = null,
                    keywords = null,
                    EventFactory.NEW_VALIDATION_RUN
                )
            }
            //
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(creator = user1.user.name)
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            val subscription = page.pageItems.first()
            val channel = subscription.data.channels.first()
            assertEquals(
                "mock",
                channel.channel
            )
            assertEquals(
                "#one",
                channel.channelConfig.getRequiredTextField("target")
            )
            assertEquals(
                setOf("new_promotion_run"),
                subscription.data.events
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
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(targetProject),
                projectEntity = this,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            branch {
                // Subscribe for events on this branch
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetBranch),
                    projectEntity = this,
                    keywords = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
                promotionLevel {
                    // Subscribe for events on this promotion
                    eventSubscriptionService.subscribe(
                        channel = mockNotificationChannel,
                        channelConfig = MockNotificationChannelConfig(targetPromotionLevel),
                        projectEntity = this,
                        keywords = null,
                        EventFactory.NEW_PROMOTION_RUN
                    )
                    // Looking for all subscriptions on this promotion, and recursively
                    val page = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(entity = toProjectEntityID(), recursive = false)
                    )
                    assertEquals(1, page.pageInfo.totalSize)
                    assertEquals(1, page.pageItems.size)
                    val targets =
                        page.pageItems.map { it.data.channels.first().channelConfig.getRequiredTextField("target") }
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
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#mock"),
                projectEntity = this,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            eventSubscriptionService.subscribe(
                channel = otherMockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#other"),
                projectEntity = this,
                keywords = null,
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
                page.pageItems.first().data.channels.first().channel
            )
        }
    }

    @Test
    fun `Filtering the subscriptions for an entity using a channel config`() {
        project {
            // Subscribe for events on this project for the two different channels configurations
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
                page.pageItems.first().data.channels.first().channel
            )
            assertEquals(
                "#one",
                page.pageItems.first().data.channels.first().channelConfig.getRequiredTextField("target")
            )
        }
    }

    @Test
    fun `Filtering the subscriptions for an entity using creation date`() {
        project {
            // Subscribe for events on this project for the two different dates
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#one"),
                projectEntity = this,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            runBlocking {
                delay(2_000L)
            }
            eventSubscriptionService.subscribe(
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig("#two"),
                projectEntity = this,
                keywords = null,
                EventFactory.NEW_PROMOTION_RUN
            )
            // Looking for all subscriptions on this promotion, and recursively
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(entity = toProjectEntityID(), createdBefore = Time.now().minusSeconds(1))
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            assertEquals(
                "mock",
                page.pageItems.first().data.channels.first().channel
            )
            assertEquals(
                "#one",
                page.pageItems.first().data.channels.first().channelConfig.getRequiredTextField("target")
            )
        }
    }

    @Test
    fun `Filtering the subscriptions for an entity using creator`() {
        project {
            // Subscribe for events on this project for the two different creators
            val user1 = asUser().with(this, ProjectSubscriptionsWrite::class.java).call {
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig("#one"),
                    projectEntity = this,
                    keywords = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
                securityService.currentSignature
            }
            val user2 = asUser().with(this, ProjectSubscriptionsWrite::class.java).call {
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig("#two"),
                    projectEntity = this,
                    keywords = null,
                    EventFactory.NEW_PROMOTION_RUN
                )
                securityService.currentSignature
            }
            assertTrue(user1.user.name != user2.user.name, "Two different users")
            //
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(entity = toProjectEntityID(), creator = user1.user.name)
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            val subscription = page.pageItems.first()
            val channel = subscription.data.channels.first()
            assertEquals(
                "mock",
                channel.channel
            )
            assertEquals(
                "#one",
                channel.channelConfig.getRequiredTextField("target")
            )
            assertEquals(
                user1.user.name,
                subscription.signature.user.name
            )
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
            //
            val page = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(entity = toProjectEntityID(), eventType = "new_promotion_run")
            )
            assertEquals(1, page.pageInfo.totalSize)
            assertEquals(1, page.pageItems.size)
            val subscription = page.pageItems.first()
            val channel = subscription.data.channels.first()
            assertEquals(
                "mock",
                channel.channel
            )
            assertEquals(
                "#one",
                channel.channelConfig.getRequiredTextField("target")
            )
            assertEquals(
                setOf("new_promotion_run"),
                subscription.data.events
            )
        }
    }


}
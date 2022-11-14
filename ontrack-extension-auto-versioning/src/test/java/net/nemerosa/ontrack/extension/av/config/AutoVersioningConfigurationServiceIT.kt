package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures
import net.nemerosa.ontrack.extension.av.event.AutoVersioningEvents
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannel
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionFilter
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class AutoVersioningConfigurationServiceIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    protected lateinit var mockNotificationChannel: MockNotificationChannel

    @Autowired
    protected lateinit var eventSubscriptionService: EventSubscriptionService

    @Test
    fun `Saving and retrieving a configuration`() {
        asAdmin {
            project {
                branch {
                    assertNull(
                        autoVersioningConfigurationService.getAutoVersioning(this),
                        "No auto versioning initially"
                    )
                    val config = AutoVersioningTestFixtures.sampleConfig()
                    autoVersioningConfigurationService.setupAutoVersioning(this, config)
                    val saved = autoVersioningConfigurationService.getAutoVersioning(this)
                    assertEquals(config, saved)
                }
            }
        }
    }

    @Test
    fun `Deleting a configuration`() {
        asAdmin {
            project {
                branch {
                    val config = AutoVersioningTestFixtures.sampleConfig()
                    autoVersioningConfigurationService.setupAutoVersioning(this, config)
                    autoVersioningConfigurationService.setupAutoVersioning(this, null)
                    val finalOne = autoVersioningConfigurationService.getAutoVersioning(this)
                    assertNull(finalOne, "Configuration has been deleted")
                }
            }
        }
    }

    @Test
    fun `Registering notifications for AV`() {
        asAdmin {
            registerAVNotifications()
        }
    }

    @Test
    fun `Registering notifications for non super admin`() {
        asAccountWithGlobalRole(Roles.GLOBAL_AUTOMATION) {
            registerAVNotifications()
        }
    }

    private fun registerAVNotifications() {
        val target = uid("t")
        val source = project {
            branch("main")
        }
        project {
            branch {
                // Setting an AV configuration with notifications
                val config = AutoVersioningConfig(
                    listOf(
                        AutoVersioningTestFixtures.sourceConfig(
                            sourceProject = source.name,
                            sourceBranch = "main",
                            notifications = listOf(
                                AutoVersioningNotification(
                                    channel = "mock",
                                    config = mapOf(
                                        "target" to target
                                    ).asJson()
                                )
                            )
                        )
                    )
                )
                autoVersioningConfigurationService.setupAutoVersioning(this, config)

                // Checks that the subscriptions are saved
                val subscriptions = eventSubscriptionService.filterSubscriptions(
                    EventSubscriptionFilter(
                        entity = toProjectEntityID(),
                        origin = "auto-versioning",
                    )
                ).pageItems.map { it.data }
                assertEquals(
                    listOf(
                        EventSubscription(
                            projectEntity = this,
                            events = setOf(
                                "auto-versioning-error",
                                "auto-versioning-success",
                                "auto-versioning-pr-merge-timeout-error",
                            ),
                            keywords = "${source.name} ${project.name} $name",
                            channel = "mock",
                            channelConfig = mapOf(
                                "target" to target,
                            ).asJson(),
                            disabled = false,
                            origin = "auto-versioning"
                        )
                    ),
                    subscriptions
                )
            }
        }
    }

    @Test
    fun `Changing the notifications for AV`() {
        asAdmin {
            val target = uid("t")
            val source = project {
                branch("main")
            }
            project {
                branch {
                    // Setting an AV configuration with notifications
                    val config = AutoVersioningConfig(
                        listOf(
                            AutoVersioningTestFixtures.sourceConfig(
                                sourceProject = source.name,
                                sourceBranch = "main",
                                notifications = listOf(
                                    AutoVersioningNotification(
                                        channel = "mock",
                                        config = mapOf(
                                            "target" to target
                                        ).asJson(),
                                        scope = listOf(
                                            AutoVersioningNotificationScope.SUCCESS
                                        )
                                    )
                                )
                            ),
                            AutoVersioningTestFixtures.sourceConfig(
                                sourceProject = source.name,
                                sourceBranch = "main",
                                notifications = listOf(
                                    AutoVersioningNotification(
                                        channel = "mock",
                                        config = mapOf(
                                            "target" to target
                                        ).asJson(),
                                        scope = listOf(
                                            AutoVersioningNotificationScope.ERROR
                                        )
                                    )
                                )
                            )
                        )
                    )
                    autoVersioningConfigurationService.setupAutoVersioning(this, config)

                    // Checks that the subscriptions are saved
                    val subscriptions = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(
                            entity = toProjectEntityID(),
                            origin = "auto-versioning",
                        )
                    ).pageItems.map { it.data }
                    assertEquals(
                        listOf(
                            EventSubscription(
                                projectEntity = this,
                                events = setOf(
                                    "auto-versioning-success",
                                ),
                                keywords = "${source.name} ${project.name} $name",
                                channel = "mock",
                                channelConfig = mapOf(
                                    "target" to target,
                                ).asJson(),
                                disabled = false,
                                origin = "auto-versioning"
                            ),
                            EventSubscription(
                                projectEntity = this,
                                events = setOf(
                                    "auto-versioning-error",
                                ),
                                keywords = "${source.name} ${project.name} $name",
                                channel = "mock",
                                channelConfig = mapOf(
                                    "target" to target,
                                ).asJson(),
                                disabled = false,
                                origin = "auto-versioning"
                            ),
                        ).toSet(),
                        subscriptions.toSet()
                    )

                    // Changing the notifications
                    val newConfig = AutoVersioningConfig(
                        listOf(
                            AutoVersioningTestFixtures.sourceConfig(
                                sourceProject = source.name,
                                sourceBranch = "main",
                                notifications = listOf(
                                    AutoVersioningNotification(
                                        channel = "mock",
                                        config = mapOf(
                                            "target" to target, // <- no change
                                        ).asJson(),
                                        scope = listOf(
                                            AutoVersioningNotificationScope.SUCCESS
                                        )
                                    )
                                )
                            ),
                            AutoVersioningTestFixtures.sourceConfig(
                                sourceProject = source.name,
                                sourceBranch = "main",
                                notifications = listOf(
                                    AutoVersioningNotification(
                                        channel = "mock",
                                        config = mapOf(
                                            "target" to "$target-error", // <- change
                                        ).asJson(),
                                        scope = listOf(
                                            AutoVersioningNotificationScope.ERROR
                                        )
                                    )
                                )
                            ),
                            // VVV New subscription
                            AutoVersioningTestFixtures.sourceConfig(
                                sourceProject = source.name,
                                sourceBranch = "main",
                                notifications = listOf(
                                    AutoVersioningNotification(
                                        channel = "mock",
                                        config = mapOf(
                                            "target" to "$target-timeout",
                                        ).asJson(),
                                        scope = listOf(
                                            AutoVersioningNotificationScope.PR_TIMEOUT
                                        )
                                    )
                                )
                            ),
                        )
                    )
                    autoVersioningConfigurationService.setupAutoVersioning(this, newConfig)

                    // Checks that the subscriptions are saved
                    val newSubscriptions = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(
                            entity = toProjectEntityID(),
                            origin = "auto-versioning",
                        )
                    ).pageItems.map { it.data }
                    assertEquals(
                        listOf(
                            EventSubscription(
                                projectEntity = this,
                                events = setOf(
                                    "auto-versioning-success",
                                ),
                                keywords = "${source.name} ${project.name} $name",
                                channel = "mock",
                                channelConfig = mapOf(
                                    "target" to target,
                                ).asJson(),
                                disabled = false,
                                origin = "auto-versioning"
                            ),
                            EventSubscription(
                                projectEntity = this,
                                events = setOf(
                                    "auto-versioning-error",
                                ),
                                keywords = "${source.name} ${project.name} $name",
                                channel = "mock",
                                channelConfig = mapOf(
                                    "target" to "$target-error",
                                ).asJson(),
                                disabled = false,
                                origin = "auto-versioning"
                            ),
                            EventSubscription(
                                projectEntity = this,
                                events = setOf(
                                    "auto-versioning-pr-merge-timeout-error",
                                ),
                                keywords = "${source.name} ${project.name} $name",
                                channel = "mock",
                                channelConfig = mapOf(
                                    "target" to "$target-timeout",
                                ).asJson(),
                                disabled = false,
                                origin = "auto-versioning"
                            ),
                        ).toSet(),
                        newSubscriptions.toSet()
                    )
                }
            }
        }
    }

    @Test
    fun `Removing the notifications when AV config is deleted`() {
        asAdmin {
            val target = uid("t")
            val source = project {
                branch("main")
            }
            project {
                branch {
                    // Setting an AV configuration with notifications
                    val config = AutoVersioningConfig(
                        listOf(
                            AutoVersioningTestFixtures.sourceConfig(
                                sourceProject = source.name,
                                sourceBranch = "main",
                                notifications = listOf(
                                    AutoVersioningNotification(
                                        channel = "mock",
                                        config = mapOf(
                                            "target" to target
                                        ).asJson(),
                                    )
                                )
                            ),
                        )
                    )
                    autoVersioningConfigurationService.setupAutoVersioning(this, config)

                    // Checks that the subscriptions are saved
                    val subscriptions = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(
                            entity = toProjectEntityID(),
                            origin = "auto-versioning",
                        )
                    ).pageItems.map { it.data }
                    assertEquals(
                        listOf(
                            EventSubscription(
                                projectEntity = this,
                                events = setOf(
                                    "auto-versioning-error",
                                    "auto-versioning-success",
                                    "auto-versioning-pr-merge-timeout-error",
                                ),
                                keywords = "${source.name} ${project.name} $name",
                                channel = "mock",
                                channelConfig = mapOf(
                                    "target" to target,
                                ).asJson(),
                                disabled = false,
                                origin = "auto-versioning"
                            ),
                        ).toSet(),
                        subscriptions.toSet()
                    )

                    // Unsetting the configuration
                    autoVersioningConfigurationService.setupAutoVersioning(this, null)

                    // Checks that the subscriptions are saved
                    val newSubscriptions = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(
                            entity = toProjectEntityID(),
                            origin = "auto-versioning",
                        )
                    ).pageItems.map { it.data }
                    assertTrue(newSubscriptions.isEmpty(), "Subscriptions are gone")
                }
            }
        }

    }

    @Test
    fun `Registering notifications keeps non AV notifications`() {
        asAdmin {
            val target = uid("t")
            val source = project {
                branch("main")
            }
            project {
                branch {
                    // Setting non AV subscriptions
                    eventSubscriptionService.subscribe(
                        channel = mockNotificationChannel,
                        channelConfig = MockNotificationChannelConfig(target = target),
                        projectEntity = this,
                        keywords = "test",
                        origin = "test",
                        AutoVersioningEvents.AUTO_VERSIONING_ERROR,
                    )
                    // Setting an AV configuration with notifications
                    val config = AutoVersioningConfig(
                        listOf(
                            AutoVersioningTestFixtures.sourceConfig(
                                sourceProject = source.name,
                                sourceBranch = "main",
                                notifications = listOf(
                                    AutoVersioningNotification(
                                        channel = "mock",
                                        config = mapOf(
                                            "target" to target
                                        ).asJson()
                                    )
                                )
                            )
                        )
                    )
                    autoVersioningConfigurationService.setupAutoVersioning(this, config)

                    // Checks that the subscriptions are saved
                    val subscriptions = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(
                            entity = toProjectEntityID(),
                            origin = "auto-versioning",
                        )
                    ).pageItems.map { it.data }
                    assertEquals(
                        listOf(
                            EventSubscription(
                                projectEntity = this,
                                events = setOf(
                                    "auto-versioning-error",
                                    "auto-versioning-success",
                                    "auto-versioning-pr-merge-timeout-error",
                                ),
                                keywords = "${source.name} ${project.name} $name",
                                channel = "mock",
                                channelConfig = mapOf(
                                    "target" to target,
                                ).asJson(),
                                disabled = false,
                                origin = "auto-versioning"
                            )
                        ),
                        subscriptions
                    )

                    // Checks that the non AV subscriptions are saved
                    val otherSubscriptions = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(
                            entity = toProjectEntityID(),
                            origin = "test",
                        )
                    ).pageItems.map { it.data }
                    assertEquals(
                        listOf(
                            EventSubscription(
                                projectEntity = this,
                                events = setOf(
                                    "auto-versioning-error",
                                ),
                                keywords = "test",
                                channel = "mock",
                                channelConfig = mapOf(
                                    "target" to target,
                                ).asJson(),
                                disabled = false,
                                origin = "test"
                            )
                        ),
                        otherSubscriptions
                    )
                }
            }
        }
    }
}
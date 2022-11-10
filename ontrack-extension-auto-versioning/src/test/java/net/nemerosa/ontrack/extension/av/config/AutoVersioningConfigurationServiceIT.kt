package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannel
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionFilter
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    }

    @Test
    fun `Changing the notifications for AV`() {
        TODO()
    }

    @Test
    fun `Removing the notifications when AV config is deleted`() {
        TODO()
    }

    @Test
    fun `Registering notifications for a given AV scope`() {
        TODO()
    }

    @Test
    fun `Registering notifications keeps non AV notifications`() {
        TODO()
    }
}
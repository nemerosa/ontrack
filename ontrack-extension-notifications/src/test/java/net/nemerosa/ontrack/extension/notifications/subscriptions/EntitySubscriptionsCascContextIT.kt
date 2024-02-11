package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.casc.CascService
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

@Disabled("Flaky")
class EntitySubscriptionsCascContextIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var cascService: CascService

    @Autowired
    private lateinit var entitySubscriptionsCascContext: EntitySubscriptionsCascContext

    @Test
    fun `Subscription for a project`() {
        val target = uid("t")
        project {
            casc(
                """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
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
                        entity = this.toProjectEntityID(),
                        origin = "casc",
                    )
                )
                assertEquals(1, subscriptions.pageItems.size)
            }
            // Checking the subscription
            branch {
                val pl = promotionLevel()
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

    @Test
    fun `Bad subscription format`() {
        val target = uid("t")
        project {
            assertFailsWith<IllegalStateException> {
                casc(
                    """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
                                        build: 1
                                      subscriptions:
                                        - events:
                                            - new_promotion_run
                                          keywords: ""
                                          channel: mock
                                          channel-config:
                                            target: "$target"
                """
                )
            }
        }
    }

    @Test
    fun `Promotions and validations are not valid`() {
        val target = uid("t")
        project {
            branch {
                assertFailsWith<IllegalStateException> {
                    casc(
                        """
                            ontrack:
                                extensions:
                                    notifications:
                                        entity-subscriptions:
                                            - entity:
                                                project: ${project.name}
                                                branch: $name
                                                promotion: PL
                                                validation: VS
                                              subscriptions:
                                                - events:
                                                    - new_promotion_run
                                                  keywords: ""
                                                  channel: mock
                                                  channel-config:
                                                    target: "$target"
                        """
                    )
                }
            }
        }
    }

    @Test
    fun `Promotions need a branch`() {
        val target = uid("t")
        project {
            branch {
                assertFailsWith<IllegalStateException> {
                    casc(
                        """
                            ontrack:
                                extensions:
                                    notifications:
                                        entity-subscriptions:
                                            - entity:
                                                project: ${project.name}
                                                promotion: PL
                                              subscriptions:
                                                - events:
                                                    - new_promotion_run
                                                  keywords: ""
                                                  channel: mock
                                                  channel-config:
                                                    target: "$target"
                        """
                    )
                }
            }
        }
    }

    @Test
    fun `Validations need a branch`() {
        val target = uid("t")
        project {
            branch {
                assertFailsWith<IllegalStateException> {
                    casc(
                        """
                            ontrack:
                                extensions:
                                    notifications:
                                        entity-subscriptions:
                                            - entity:
                                                project: ${project.name}
                                                validation: VS
                                              subscriptions:
                                                - events:
                                                    - new_promotion_run
                                                  keywords: ""
                                                  channel: mock
                                                  channel-config:
                                                    target: "$target"
                        """
                    )
                }
            }
        }
    }

    @Test
    fun `Rendering without a template`() {
        val target = uid("t")
        project {
            casc(
                """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
                                      subscriptions:
                                        - events:
                                            - new_promotion_run
                                          keywords: "SILVER"
                                          channel: mock
                                          channel-config:
                                            target: "$target-silver"
                                        - events:
                                            - new_promotion_run
                                          keywords: "GOLD"
                                          channel: mock
                                          channel-config:
                                            target: "$target-gold"
                """
            )
            // Rendering
            val json = entitySubscriptionsCascContext.render()
            assertEquals(
                listOf(
                    mapOf(
                        "entity" to mapOf(
                            "project" to name,
                            "branch" to null,
                            "promotion" to null,
                            "validation" to null,
                        ),
                        "subscriptions" to listOf(
                            mapOf(
                                "events" to listOf("new_promotion_run"),
                                "keywords" to "SILVER",
                                "channel" to "mock",
                                "channel-config" to mapOf(
                                    "target" to "$target-silver"
                                ),
                                "disabled" to null,
                                "contentTemplate" to null,
                            ),
                            mapOf(
                                "events" to listOf("new_promotion_run"),
                                "keywords" to "GOLD",
                                "channel" to "mock",
                                "channel-config" to mapOf(
                                    "target" to "$target-gold"
                                ),
                                "disabled" to null,
                                "contentTemplate" to null,
                            ),
                        )
                    )
                ).asJson(),
                json
            )
        }
    }

    @Test
    fun `Rendering with a template`() {
        val target = uid("t")
        project {
            casc(
                """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
                                      subscriptions:
                                        - events:
                                            - new_promotion_run
                                          keywords: "SILVER"
                                          channel: mock
                                          channel-config:
                                            target: "$target-silver"
                                          contentTemplate: |
                                            This is my template.
                """.trimIndent()
            )
            // Rendering
            val json = entitySubscriptionsCascContext.render()
            assertEquals(
                listOf(
                    mapOf(
                        "entity" to mapOf(
                            "project" to name,
                            "branch" to null,
                            "promotion" to null,
                            "validation" to null,
                        ),
                        "subscriptions" to listOf(
                            mapOf(
                                "events" to listOf("new_promotion_run"),
                                "keywords" to "SILVER",
                                "channel" to "mock",
                                "channel-config" to mapOf(
                                    "target" to "$target-silver"
                                ),
                                "disabled" to null,
                                "contentTemplate" to "This is my template.",
                            ),
                        )
                    )
                ).asJson(),
                json
            )
        }
    }

    @Test
    fun `Updating the subscription for a project`() {
        val target = uid("t")
        project {
            // Initial subscriptions
            casc(
                """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
                                      subscriptions:
                                        - events:
                                            - new_promotion_run
                                          keywords: "GOLD"
                                          channel: mock
                                          channel-config:
                                            target: "$target"
                                        - events:
                                            - new_promotion_run
                                          keywords: "SILVER"
                                          channel: mock
                                          channel-config:
                                            target: "$target"
                """
            )
            // Checks that we can find these subscriptions
            asAdmin {
                val subscriptions = eventSubscriptionService.filterSubscriptions(
                    EventSubscriptionFilter(
                        entity = this.toProjectEntityID(),
                        origin = "casc",
                    )
                ).pageItems.map { it.data.keywords }
                assertEquals(
                    setOf("GOLD", "SILVER"),
                    subscriptions.toSet()
                )
            }
            // New subscriptions
            casc(
                """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
                                      subscriptions:
                                        - events:
                                            - new_promotion_run
                                          keywords: "PLATINUM"
                                          channel: mock
                                          channel-config:
                                            target: "$target"
                                        - events:
                                            - new_promotion_run
                                          keywords: "SILVER"
                                          channel: mock
                                          channel-config:
                                            target: "$target-silver"
                """
            )
            // Checks that we can find this subscription
            asAdmin {
                val subscriptions = eventSubscriptionService.filterSubscriptions(
                    EventSubscriptionFilter(
                        entity = this.toProjectEntityID(),
                        origin = "casc",
                    )
                ).pageItems.map { it.data.keywords }
                assertEquals(
                    setOf("PLATINUM", "SILVER"),
                    subscriptions.toSet()
                )
            }
        }
    }

    @Test
    fun `Deleting an entity from the list of subscriptions`() {
        val target = uid("t")
        project {
            // Initial subscriptions
            casc(
                """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
                                      subscriptions:
                                        - events:
                                            - new_promotion_run
                                          keywords: "GOLD"
                                          channel: mock
                                          channel-config:
                                            target: "$target"
                                        - events:
                                            - new_promotion_run
                                          keywords: "SILVER"
                                          channel: mock
                                          channel-config:
                                            target: "$target"
                """
            )
            // Checks that we can find these subscriptions
            asAdmin {
                val subscriptions = eventSubscriptionService.filterSubscriptions(
                    EventSubscriptionFilter(
                        entity = this.toProjectEntityID(),
                        origin = "casc",
                    )
                ).pageItems.map { it.data.keywords }
                assertEquals(
                    setOf("GOLD", "SILVER"),
                    subscriptions.toSet()
                )
            }
            // Removing the entity-level
            casc(
                """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions: []
                """
            )
            // Checks that we cannot find these subscriptions any longer
            asAdmin {
                val subscriptions = eventSubscriptionService.filterSubscriptions(
                    EventSubscriptionFilter(
                        entity = this.toProjectEntityID(),
                        origin = "casc",
                    )
                ).pageItems
                assertTrue(
                    subscriptions.isEmpty(),
                    "Subscriptions are gone"
                )
            }
        }
    }

    @Test
    fun `Subscription for a branch`() {
        val target = uid("t")
        project {
            branch {
                casc(
                    """
                        ontrack:
                            extensions:
                                notifications:
                                    entity-subscriptions:
                                        - entity:
                                            project: ${project.name}
                                            branch: $name
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
                            entity = this.toProjectEntityID(),
                            origin = "casc",
                        )
                    )
                    assertEquals(1, subscriptions.pageItems.size)
                }
                // Checking the subscription
                val pl = promotionLevel()
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

    @Test
    fun `Subscription for a validation`() {
        val target = uid("t")
        project {
            branch {
                val vs = validationStamp()
                casc(
                    """
                        ontrack:
                            extensions:
                                notifications:
                                    entity-subscriptions:
                                        - entity:
                                            project: ${project.name}
                                            branch: $name
                                            validation: ${vs.name}
                                          subscriptions:
                                            - events:
                                                - new_validation_run
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
                            entity = vs.toProjectEntityID(),
                            origin = "casc",
                        )
                    )
                    assertEquals(1, subscriptions.pageItems.size)
                }
                // Checking the subscription
                build {
                    // Creating the event
                    validate(vs)
                    // Checking the notification
                    assertNotNull(mockNotificationChannel.messages[target], "Received notification on promotion") {
                        assertEquals(
                            "Build $name has run for ${vs.name} with status Passed in branch ${branch.name} in ${project.name}.",
                            it.first()
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Subscription for a failed validation`() {
        val target = uid("t")
        project {
            branch {
                val vs = validationStamp()
                casc(
                    """
                        ontrack:
                            extensions:
                                notifications:
                                    entity-subscriptions:
                                        - entity:
                                            project: ${project.name}
                                            branch: $name
                                            validation: ${vs.name}
                                          subscriptions:
                                            - events:
                                                - new_validation_run
                                              keywords: "failed"
                                              channel: mock
                                              channel-config:
                                                target: "$target"
                    """
                )
                // Checks that we can find this subscription
                asAdmin {
                    val subscriptions = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(
                            entity = vs.toProjectEntityID(),
                            origin = "casc",
                        )
                    )
                    assertEquals(1, subscriptions.pageItems.size)
                }
                // Checking the subscription
                build {
                    // Creating the event for a successful validation
                    validate(vs)
                    // Checking that there is no notification
                    assertNull(
                        mockNotificationChannel.messages[target],
                        "Received no notification on passed validation"
                    )
                    // Creating the event for a failed validation
                    validate(vs, ValidationRunStatusID.STATUS_FAILED)
                    // Checking the notification
                    assertNotNull(
                        mockNotificationChannel.messages[target],
                        "Received notification on failed notification"
                    ) {
                        assertEquals(
                            "Build $name has run for ${vs.name} with status Failed in branch ${branch.name} in ${project.name}.",
                            it.first()
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Subscription for a non-existing project`() {
        val target = uid("t")
        val projectName = uid("p")
        casc(
            """
                ontrack:
                    extensions:
                        notifications:
                            entity-subscriptions:
                                - entity:
                                    project: $projectName
                                  subscriptions:
                                    - events:
                                        - new_promotion_run
                                      keywords: ""
                                      channel: mock
                                      channel-config:
                                        target: "$target"
            """
        )
        // Creating the project now
        project(NameDescription.nd(projectName, "")) {
            // Checks that we cannot find any subscription
            asAdmin {
                val subscriptions = eventSubscriptionService.filterSubscriptions(
                    EventSubscriptionFilter(
                        entity = this.toProjectEntityID(),
                        origin = "casc",
                    )
                )
                assertEquals(0, subscriptions.pageItems.size)
            }
            // Running the Casc again
            casc(
                """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
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
                        entity = this.toProjectEntityID(),
                        origin = "casc",
                    )
                )
                assertEquals(1, subscriptions.pageItems.size)
            }
            // Checking the subscription
            branch {
                val pl = promotionLevel()
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
                            origin = "casc",
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

    @Test
    fun `Subscription for a promotion with some content template`() {
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
                                              contentTemplate: |
                                                Change log for ${'$'}{build.release}
                                                
                                                ${'$'}{changelog?format=text}
                                                
                                                ${'$'}{changelog?format=text&project=dep-01}
                    """.trimIndent()
                )
                // Checks that we can find this subscription
                asAdmin {
                    val subscriptions = eventSubscriptionService.filterSubscriptions(
                        EventSubscriptionFilter(
                            entity = pl.toProjectEntityID(),
                            origin = "casc",
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
                    assertEquals(
                        """
                            Change log for ${'$'}{build.release}
                            
                            ${'$'}{changelog?format=text}
                            
                            ${'$'}{changelog?format=text&project=dep-01}
                        """.trimIndent(),
                        subscription.data.contentTemplate
                    )
                }
            }
        }
    }

    @Test
    fun `Duplicated entity`() {
        val target = uid("t")
        project {
            val ex = assertFails {
                casc(
                    """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
                                      subscriptions:
                                        - events:
                                            - new_promotion_run
                                          keywords: ""
                                          channel: mock
                                          channel-config:
                                            target: "$target"
                                    - entity:
                                        project: $name
                                      subscriptions:
                                        - events:
                                            - new_promotion_run
                                          keywords: "GOLD"
                                          channel: mock
                                          channel-config:
                                            target: "$target"
                    """
                )
            }
            assertEquals(
                """
                Duplicate entities in the notifications:
                 * $name
            """.trimIndent(), ex.message
            )
        }
    }

    @Test
    fun `Subscription for a project changing only the channel configuration`() {
        val target = uid("t")
        val newTarget = uid("nt")
        project {
            // Initial subscription
            casc(
                """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
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
                        entity = this.toProjectEntityID(),
                        origin = "casc",
                    )
                ).pageItems.map { it.data.channelConfig.getRequiredTextField("target") }
                assertEquals(listOf(target), subscriptions)
            }
            // Changing only the target channel
            casc(
                """
                    ontrack:
                        extensions:
                            notifications:
                                entity-subscriptions:
                                    - entity:
                                        project: $name
                                      subscriptions:
                                        - events:
                                            - new_promotion_run
                                          keywords: ""
                                          channel: mock
                                          channel-config:
                                            target: "$newTarget"
                """
            )
            // Checks that we can find this subscription and only this one
            asAdmin {
                val subscriptions = eventSubscriptionService.filterSubscriptions(
                    EventSubscriptionFilter(
                        entity = this.toProjectEntityID(),
                        origin = "casc",
                    )
                ).pageItems.map { it.data.channelConfig.getRequiredTextField("target") }
                assertEquals(listOf(newTarget), subscriptions)
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
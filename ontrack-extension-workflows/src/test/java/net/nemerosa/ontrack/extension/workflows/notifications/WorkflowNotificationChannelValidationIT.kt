package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionConfigException
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.EventFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFailsWith

class WorkflowNotificationChannelValidationIT : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var eventSubscriptionService: EventSubscriptionService

    @Autowired
    private lateinit var workflowNotificationChannel: WorkflowNotificationChannel

    @Test
    fun `Validation of the workflow before saving, wrong syntax`() {
        asAdmin {
            project {
                branch {
                    promotionLevel {
                        assertFailsWith<EventSubscriptionConfigException> {
                            eventSubscriptionService.subscribe(
                                EventSubscription(
                                    name = "test",
                                    channel = workflowNotificationChannel.type,
                                    channelConfig = mapOf(
                                        "workflow" to mapOf(
                                            "name" to "Some workflows",
                                            "nodes" to listOf(
                                                mapOf(
                                                    "id" to "start",
                                                    "executorId" to "mock",
                                                    "data" to mapOf(
                                                        "text" to "Some text"
                                                    )
                                                ),
                                                mapOf(
                                                    "id" to "end",
                                                    "executorId" to "mock",
                                                    "parents" to listOf(
                                                        "start", // <-- wrong, should be id: start
                                                    ),
                                                    "data" to mapOf(
                                                        "text" to "Some end text"
                                                    )
                                                ),
                                            )
                                        )
                                    ).asJson(),
                                    events = setOf(EventFactory.NEW_PROMOTION_RUN.id),
                                    projectEntity = this,
                                    keywords = null,
                                    origin = "test",
                                    disabled = false,
                                    contentTemplate = null,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Validation of the workflow before saving, unknown executor`() {
        asAdmin {
            project {
                branch {
                    promotionLevel {
                        assertFailsWith<EventSubscriptionConfigException> {
                            eventSubscriptionService.subscribe(
                                name = "test",
                                channel = workflowNotificationChannel,
                                channelConfig = WorkflowNotificationChannelConfig(
                                    workflow = WorkflowParser.parseYamlWorkflow(
                                        """
                                            name: Some workflow
                                            nodes:
                                              - id: start
                                                executorId: unknown
                                                data: {}
                                        """.trimIndent()
                                    )
                                ),
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
    }

    @Test
    fun `Validation of the workflow before saving, unknown parent`() {
        asAdmin {
            project {
                branch {
                    promotionLevel {
                        assertFailsWith<EventSubscriptionConfigException> {
                            eventSubscriptionService.subscribe(
                                name = "test",
                                channel = workflowNotificationChannel,
                                channelConfig = WorkflowNotificationChannelConfig(
                                    workflow = WorkflowParser.parseYamlWorkflow(
                                        """
                                            name: Some workflow
                                            nodes:
                                              - id: start
                                                executorId: mock
                                                data:
                                                  text: Start
                                              - id: end
                                                parents:
                                                  - id: unknown
                                                executorId: mock
                                                data:
                                                  text: End
                                        """.trimIndent()
                                    )
                                ),
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
    }

    @Test
    fun `Validation of the workflow before saving, no starting node`() {
        asAdmin {
            project {
                branch {
                    promotionLevel {
                        assertFailsWith<EventSubscriptionConfigException> {
                            eventSubscriptionService.subscribe(
                                name = "test",
                                channel = workflowNotificationChannel,
                                channelConfig = WorkflowNotificationChannelConfig(
                                    workflow = WorkflowParser.parseYamlWorkflow(
                                        """
                                            name: Some workflow
                                            nodes:
                                              - id: start
                                                parents:
                                                  - id: end
                                                executorId: mock
                                                data:
                                                  text: Start
                                              - id: end
                                                parents:
                                                  - id: start
                                                executorId: mock
                                                data:
                                                  text: End
                                        """.trimIndent()
                                    )
                                ),
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
    }

    @Test
    fun `Validation of the workflow before saving, cycle issue`() {
        asAdmin {
            project {
                branch {
                    promotionLevel {
                        assertFailsWith<EventSubscriptionConfigException> {
                            eventSubscriptionService.subscribe(
                                name = "test",
                                channel = workflowNotificationChannel,
                                channelConfig = WorkflowNotificationChannelConfig(
                                    workflow = WorkflowParser.parseYamlWorkflow(
                                        """
                                            name: Some workflow
                                            nodes:
                                              - id: start
                                                executorId: mock
                                                data:
                                                  text: Start
                                              - id: end
                                                parents:
                                                  - id: end
                                                executorId: mock
                                                data:
                                                  text: End
                                        """.trimIndent()
                                    )
                                ),
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
    }

    @Test
    fun `Validation of the workflow before saving, wrong configuration`() {
        asAdmin {
            project {
                branch {
                    promotionLevel {
                        assertFailsWith<EventSubscriptionConfigException> {
                            eventSubscriptionService.subscribe(
                                name = "test",
                                channel = workflowNotificationChannel,
                                channelConfig = WorkflowNotificationChannelConfig(
                                    workflow = WorkflowParser.parseYamlWorkflow(
                                        """
                                            name: Some workflow
                                            nodes:
                                              - id: start
                                                executorId: mock
                                                data:
                                                  text: ""
                                        """.trimIndent()
                                    )
                                ),
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
    }

    @Test
    fun `Validation of the workflow before saving, wrong configuration in notification node`() {
        asAdmin {
            project {
                branch {
                    promotionLevel {
                        assertFailsWith<EventSubscriptionConfigException> {
                            eventSubscriptionService.subscribe(
                                name = "test",
                                channel = workflowNotificationChannel,
                                channelConfig = WorkflowNotificationChannelConfig(
                                    workflow = WorkflowParser.parseYamlWorkflow(
                                        """
                                            name: Some workflow
                                            nodes:
                                              - id: start
                                                executorId: notification
                                                data:
                                                  channel: mock
                                                  channelConfig:
                                                    target: ""
                                        """.trimIndent()
                                    )
                                ),
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
    }


}
package net.nemerosa.ontrack.extension.workflows.deliverymap

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationSource
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationSourceDataType
import net.nemerosa.ontrack.extension.notifications.model.createData
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordingService
import net.nemerosa.ontrack.extension.notifications.recording.toNotificationRecordResult
import net.nemerosa.ontrack.extension.workflows.AbstractWorkflowTestSupport
import net.nemerosa.ontrack.extension.workflows.notifications.EntityWorkflowInstanceService
import net.nemerosa.ontrack.extension.workflows.notifications.WorkflowNotificationChannel
import net.nemerosa.ontrack.extension.workflows.notifications.WorkflowNotificationChannelOutput
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The workflows of a branch's promotions, on the branch's delivery map.
 *
 * Both halves are exercised against a real database: the batched lookup which resolves every
 * promotion run of a branch at once, and the shape the map hands the frontend.
 */
internal class WorkflowDeliveryMapIT : AbstractWorkflowTestSupport() {

    @Autowired
    private lateinit var notificationRecordingService: NotificationRecordingService

    @Autowired
    private lateinit var entityWorkflowInstanceService: EntityWorkflowInstanceService

    @Autowired
    private lateinit var mockNotificationSource: MockNotificationSource

    @Autowired
    private lateinit var eventFactory: EventFactory

    private fun workflowYaml(name: String) = """
        name: $name
        nodes:
            - id: start
              executorId: mock
              data:
                text: Start
    """.trimIndent()

    private fun recordWorkflowNotification(
        promotionRun: PromotionRun,
        instanceId: String,
        timestamp: LocalDateTime = Time.now(),
    ) {
        asAdmin {
            notificationRecordingService.record(
                NotificationRecord(
                    id = UUID.randomUUID().toString(),
                    source = mockNotificationSource.createData(MockNotificationSourceDataType(text = "test")),
                    timestamp = timestamp,
                    channel = WorkflowNotificationChannel.TYPE,
                    channelConfig = mapOf("workflow" to mapOf("name" to "test")).asJson(),
                    event = eventFactory.newPromotionRun(promotionRun).asJson(),
                    result = NotificationResult.async(
                        WorkflowNotificationChannelOutput(workflowInstanceId = instanceId)
                    ).toNotificationRecordResult(),
                )
            )
        }
    }

    @Test
    fun `Every promotion run of a branch is resolved in one batch, each keeping its own workflows`() {
        val firstInstance = workflowTestSupport.registerLaunchAndWaitForWorkflow(workflowYaml(uid("wf-")))
        val secondInstance = workflowTestSupport.registerLaunchAndWaitForWorkflow(workflowYaml(uid("wf-")))
        asAdmin {
            project {
                branch {
                    val silver = promotionLevel("SILVER")
                    val gold = promotionLevel("GOLD")
                    val silverRun = build().promote(silver)
                    val goldRun = build().promote(gold)
                    recordWorkflowNotification(silverRun, firstInstance)
                    recordWorkflowNotification(goldRun, secondInstance)

                    val instances = entityWorkflowInstanceService.findWorkflowInstancesByEntities(
                        listOf(silverRun.toProjectEntityID(), goldRun.toProjectEntityID())
                    )

                    assertEquals(
                        listOf(firstInstance),
                        instances[silverRun.toProjectEntityID()]?.map { it.id },
                    )
                    assertEquals(
                        listOf(secondInstance),
                        instances[goldRun.toProjectEntityID()]?.map { it.id },
                    )
                }
            }
        }
    }

    @Test
    fun `The record cap applies per entity, so a busy run never starves another`() {
        // The arrangement a GLOBAL cap of MAX_RECORDS would get wrong, and the reason this is a
        // windowed query rather than one `IN` clause with a limit: the quiet run's only record is
        // the OLDEST of the lot, so a single cap across both entities would drop it entirely and
        // the map would say that promotion set off no workflow at all.
        val instance = workflowTestSupport.registerLaunchAndWaitForWorkflow(workflowYaml(uid("wf-")))
        asAdmin {
            project {
                branch {
                    val silver = promotionLevel("SILVER")
                    val gold = promotionLevel("GOLD")
                    val busyRun = build().promote(silver)
                    val quietRun = build().promote(gold)

                    // Timestamps are set explicitly: `Time.now()` on two records written in the same
                    // instant leaves the ordering the cap depends on to chance
                    val now = Time.now()
                    recordWorkflowNotification(quietRun, instance, now.minusDays(1))
                    repeat(EntityWorkflowInstanceService.MAX_RECORDS) { index ->
                        recordWorkflowNotification(busyRun, instance, now.minusSeconds(index.toLong()))
                    }

                    val instances = entityWorkflowInstanceService.findWorkflowInstancesByEntities(
                        listOf(busyRun.toProjectEntityID(), quietRun.toProjectEntityID())
                    )

                    assertEquals(listOf(instance), instances[busyRun.toProjectEntityID()]?.map { it.id })
                    assertEquals(listOf(instance), instances[quietRun.toProjectEntityID()]?.map { it.id })
                }
            }
        }
    }

    @Test
    fun `An entity with no workflow at all is absent from the batch`() {
        asAdmin {
            project {
                branch {
                    val run = build().promote(promotionLevel())
                    assertTrue(
                        entityWorkflowInstanceService
                            .findWorkflowInstancesByEntities(listOf(run.toProjectEntityID()))
                            .isEmpty()
                    )
                }
            }
        }
    }

    @Test
    fun `The delivery map draws a workflow checkpoint emitted by its promotion level`() {
        val workflowName = uid("wf-")
        val instanceId = workflowTestSupport.registerLaunchAndWaitForWorkflow(workflowYaml(workflowName))
        asAdmin {
            project {
                branch {
                    val silver = promotionLevel("SILVER")
                    recordWorkflowNotification(build().promote(silver), instanceId)
                    run(
                        """{
                            branch(id: $id) {
                                deliveryMap {
                                    checkpoints { id type name data }
                                    edges { kind source target }
                                }
                            }
                        }"""
                    ) { data ->
                        val map = data.path("branch").path("deliveryMap")
                        val checkpoint = map.path("checkpoints")
                            .first { it.path("type").asText() == "workflow" }
                        assertEquals("workflow:${silver.id}:$workflowName", checkpoint.path("id").asText())
                        assertEquals(workflowName, checkpoint.path("name").asText())
                        assertEquals(
                            instanceId,
                            checkpoint.path("data").path("workflowInstanceId").asText(),
                        )
                        assertEquals("SUCCESS", checkpoint.path("data").path("status").asText())

                        val edge = map.path("edges").first { it.path("kind").asText() == "EMITS" }
                        assertEquals("promotion-level:${silver.id}", edge.path("source").asText())
                        assertEquals("workflow:${silver.id}:$workflowName", edge.path("target").asText())
                    }
                }
            }
        }
    }

    @Test
    fun `A promotion level which has never been promoted draws no workflow`() {
        asAdmin {
            project {
                branch {
                    promotionLevel("SILVER")
                    run(
                        """{
                            branch(id: $id) {
                                deliveryMap {
                                    checkpoints { type }
                                }
                            }
                        }"""
                    ) { data ->
                        assertTrue(
                            data.path("branch").path("deliveryMap").path("checkpoints")
                                .none { it.path("type").asText() == "workflow" }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `The promotion level checkpoint names the run its workflows belong to`() {
        asAdmin {
            project {
                branch {
                    val silver = promotionLevel("SILVER")
                    val promotionRun = build().promote(silver)
                    run(
                        """{
                            branch(id: $id) {
                                deliveryMap {
                                    checkpoints { type data }
                                }
                            }
                        }"""
                    ) { data ->
                        val checkpoint = data.path("branch").path("deliveryMap").path("checkpoints")
                            .first { it.path("type").asText() == "promotion-level" }
                        assertEquals(
                            promotionRun.id(),
                            checkpoint.path("data").path("promotionRunId").asInt(),
                        )
                    }
                }
            }
        }
    }
}

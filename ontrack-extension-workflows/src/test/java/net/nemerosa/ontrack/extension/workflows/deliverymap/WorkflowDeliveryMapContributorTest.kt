package net.nemerosa.ontrack.extension.workflows.deliverymap

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNodeStatus
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStatus
import net.nemerosa.ontrack.extension.workflows.notifications.EntityWorkflowInstanceService
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapEdgeKind
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WorkflowDeliveryMapContributorTest {

    private val project = Project.of(nd("P", "")).withId(ID.of(1))
    private val branch = Branch.of(project, nd("main", "")).withId(ID.of(1))
    private val build = Build.of(branch, nd("1", ""), Signature.of("test")).withId(ID.of(10))

    private val silver = PromotionLevel.of(branch, nd("SILVER", "")).withId(ID.of(2))
    private val gold = PromotionLevel.of(branch, nd("GOLD", "")).withId(ID.of(3))

    private val silverRun = promotionRun(100, silver)
    private val goldRun = promotionRun(101, gold)

    private lateinit var structureService: StructureService
    private lateinit var entityWorkflowInstanceService: EntityWorkflowInstanceService
    private lateinit var contributor: WorkflowDeliveryMapContributor

    @BeforeEach
    fun setup() {
        structureService = mockk()
        every { structureService.getPromotionLevelListForBranch(branch.id) } returns listOf(silver, gold)
        every { structureService.getLastPromotionRunForPromotionLevel(silver) } returns silverRun
        every { structureService.getLastPromotionRunForPromotionLevel(gold) } returns goldRun

        entityWorkflowInstanceService = mockk()
        every { entityWorkflowInstanceService.findWorkflowInstancesByEntities(any()) } returns emptyMap()

        contributor = WorkflowDeliveryMapContributor(
            structureService = structureService,
            entityWorkflowInstanceService = entityWorkflowInstanceService,
        )
    }

    @Test
    fun `A workflow fired by a promotion is drawn as a checkpoint of its own`() {
        givenWorkflows(silverRun to listOf(instance("i-1", "Canary verification")))

        val contribution = contributor.contribute(branch)

        val checkpoint = contribution.checkpoints.single()
        assertEquals("workflow:2:Canary verification", checkpoint.id)
        assertEquals(WorkflowDeliveryMapCheckpoints.WORKFLOW, checkpoint.type)
        assertEquals("Canary verification", checkpoint.name)
    }

    @Test
    fun `The workflow checkpoint carries the run to link to and where it got to`() {
        givenWorkflows(silverRun to listOf(instance("i-1", "Canary verification")))

        val data = contributor.contribute(branch).checkpoints.single()
            .data.parse<WorkflowCheckpointData>()

        assertEquals("i-1", data.workflowInstanceId)
        assertEquals(WorkflowInstanceStatus.SUCCESS, data.status)
        assertEquals(START, data.startTime)
        assertEquals(2000, data.durationMs)
    }

    @Test
    fun `A running workflow carries its start time and no duration yet`() {
        // `durationMs` is 0 until the last node ends, which is why the payload carries the start
        givenWorkflows(silverRun to listOf(instance("i-1", "Canary verification", running = true)))

        val data = contributor.contribute(branch).checkpoints.single()
            .data.parse<WorkflowCheckpointData>()

        assertEquals(WorkflowInstanceStatus.RUNNING, data.status)
        assertEquals(START, data.startTime)
        assertEquals(0, data.durationMs)
    }

    @Test
    fun `The promotion emits its workflow, and never requires it`() {
        // A promotion workflow runs after the promotion is granted and nothing waits for it
        givenWorkflows(silverRun to listOf(instance("i-1", "Canary verification")))

        val edge = contributor.contribute(branch).edges.single()

        assertEquals(DeliveryMapEdgeKind.EMITS, edge.kind)
        assertEquals("promotion-level:2", edge.source)
        assertEquals("workflow:2:Canary verification", edge.target)
    }

    @Test
    fun `A workflow checkpoint has no arrival of its own`() {
        // Its build would always be the one its promotion level already names
        givenWorkflows(silverRun to listOf(instance("i-1", "Canary verification")))
        assertNull(contributor.contribute(branch).checkpoints.single().arrival)
    }

    @Test
    fun `Each promotion level draws the workflows of its own run`() {
        givenWorkflows(
            silverRun to listOf(instance("i-1", "Canary verification")),
            goldRun to listOf(instance("i-2", "Release announcement")),
        )

        val contribution = contributor.contribute(branch)

        assertEquals(
            setOf("workflow:2:Canary verification", "workflow:3:Release announcement"),
            contribution.checkpoints.map { it.id }.toSet(),
        )
        assertEquals(
            setOf(
                "promotion-level:2" to "workflow:2:Canary verification",
                "promotion-level:3" to "workflow:3:Release announcement",
            ),
            contribution.edges.map { it.source to it.target }.toSet(),
        )
    }

    @Test
    fun `Two workflows of one run sharing a name collapse into one checkpoint`() {
        // The accepted cost of keying on the name: there is no configured entity to key on instead
        givenWorkflows(
            silverRun to listOf(
                instance("i-latest", "Canary verification"),
                instance("i-older", "Canary verification"),
            )
        )

        val checkpoint = contributor.contribute(branch).checkpoints.single()

        // Most recent first, so the latest run wins
        assertEquals("i-latest", checkpoint.data.parse<WorkflowCheckpointData>().workflowInstanceId)
    }

    @Test
    fun `A promotion level which has never been promoted draws nothing`() {
        // A workflow is only reachable through the notification records of the runs which fired it
        every { structureService.getLastPromotionRunForPromotionLevel(silver) } returns null
        every { structureService.getLastPromotionRunForPromotionLevel(gold) } returns null

        val contribution = contributor.contribute(branch)

        assertTrue(contribution.checkpoints.isEmpty())
        assertTrue(contribution.edges.isEmpty())
    }

    @Test
    fun `A branch whose promotions fired no workflow draws nothing`() {
        val contribution = contributor.contribute(branch)
        assertTrue(contribution.checkpoints.isEmpty())
        assertTrue(contribution.edges.isEmpty())
    }

    @Test
    fun `Every promotion run is resolved in one batch`() {
        // 4N queries for a branch is what the batching exists to avoid (#1711)
        givenWorkflows(silverRun to listOf(instance("i-1", "Canary verification")))

        contributor.contribute(branch)

        io.mockk.verify(exactly = 1) {
            entityWorkflowInstanceService.findWorkflowInstancesByEntities(
                setOf(silverRun.toProjectEntityID(), goldRun.toProjectEntityID())
            )
        }
    }

    private fun givenWorkflows(vararg runs: Pair<PromotionRun, List<WorkflowInstance>>) {
        every { entityWorkflowInstanceService.findWorkflowInstancesByEntities(any()) } returns
                runs.associate { (run, instances) -> run.toProjectEntityID() to instances }
    }

    private fun promotionRun(id: Int, promotionLevel: PromotionLevel) = PromotionRun(
        id = ID.of(id),
        build = build,
        promotionLevel = promotionLevel,
        signature = Signature.of(START, "test"),
        description = null,
    )

    private fun instance(id: String, name: String, running: Boolean = false) = WorkflowInstance(
        id = id,
        timestamp = START,
        workflow = Workflow(name = name, nodes = emptyList()),
        event = SerializableEvent(
            id = 0,
            eventType = "new_promotion_run",
            signature = null,
            entities = emptyMap(),
            extraEntities = emptyMap(),
            ref = null,
            values = emptyMap(),
        ),
        contexts = emptyMap(),
        nodesExecutions = listOf(
            WorkflowInstanceNode(
                id = "node",
                status = if (running) WorkflowInstanceNodeStatus.STARTED else WorkflowInstanceNodeStatus.SUCCESS,
                startTime = START,
                endTime = if (running) null else START.plusSeconds(2),
                output = null,
                error = null,
            )
        ),
    )

    companion object {
        private val START: LocalDateTime = LocalDateTime.of(2026, 9, 1, 10, 0)
    }
}

package net.nemerosa.ontrack.extension.environments.deliverymap

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.environments.EnvironmentTestFixtures
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotTestFixtures
import net.nemerosa.ontrack.extension.environments.rules.core.BranchPatternSlotAdmissionRule
import net.nemerosa.ontrack.extension.environments.rules.core.BranchPatternSlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.rules.core.EnvironmentSlotAdmissionRule
import net.nemerosa.ontrack.extension.environments.rules.core.EnvironmentSlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.rules.core.PromotionSlotAdmissionRule
import net.nemerosa.ontrack.extension.environments.rules.core.PromotionSlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapEdgeKind
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SlotDeliveryMapContributorTest {

    private val project = Project.of(nd("P", "")).withId(ID.of(1))
    private val main = Branch.of(project, nd("main", "")).withId(ID.of(1))
    private val maintenance = Branch.of(project, nd("release-1.3", "")).withId(ID.of(2))

    private val gold = PromotionLevel.of(main, nd("GOLD", "")).withId(ID.of(3))

    private val staging = EnvironmentTestFixtures.testEnvironment(name = "staging", order = 100)
    private val production = EnvironmentTestFixtures.testEnvironment(name = "production", order = 200)

    private val stagingSlot = SlotTestFixtures.testSlot(env = staging, project = project)
    private val productionSlot = SlotTestFixtures.testSlot(env = production, project = project)

    private lateinit var slotService: SlotService
    private lateinit var structureService: StructureService
    private lateinit var contributor: SlotDeliveryMapContributor

    @BeforeEach
    fun setup() {
        slotService = mockk()
        every { slotService.findSlotsByProject(project) } returns setOf(productionSlot, stagingSlot)
        every { slotService.getAdmissionRuleConfigs(any()) } returns emptyList()
        every { slotService.getLastDeployedPipeline(any()) } returns null

        structureService = mockk()
        every { structureService.findPromotionLevelByName(any(), any(), any()) } returns Optional.empty()

        contributor = SlotDeliveryMapContributor(
            slotService = slotService,
            // The real rules, so that the map reads a stored configuration exactly as the
            // deployment does. Their collaborators are mocked because parsing a configuration and
            // testing a branch name against a pattern touch none of them.
            promotionSlotAdmissionRule = PromotionSlotAdmissionRule(structureService),
            environmentSlotAdmissionRule = EnvironmentSlotAdmissionRule(mockk(), mockk()),
            branchPatternSlotAdmissionRule = BranchPatternSlotAdmissionRule(mockk(), mockk()),
            structureService = structureService,
        )
    }

    private fun rules(slot: Slot, vararg configs: Pair<String, Any>) {
        every { slotService.getAdmissionRuleConfigs(slot) } returns configs.map { (ruleId, ruleConfig) ->
            SlotAdmissionRuleConfig(
                slot = slot,
                description = null,
                ruleId = ruleId,
                ruleConfig = ruleConfig.asJson(),
            )
        }
    }

    private fun deployed(slot: Slot, build: Build, time: LocalDateTime = LocalDateTime.of(2026, 9, 1, 10, 0)) {
        every { slotService.getLastDeployedPipeline(slot) } returns SlotPipeline(
            number = 1,
            slot = slot,
            build = build,
            start = time,
            end = time,
        )
    }

    @Test
    fun `Every slot of the project is a checkpoint, in environment order`() {
        val contribution = contributor.contribute(main)
        assertEquals(
            listOf("staging", "production"),
            contribution.checkpoints.map { it.name },
        )
        assertEquals(
            listOf("slot:${stagingSlot.id}", "slot:${productionSlot.id}"),
            contribution.checkpoints.map { it.id },
        )
        assertTrue(contribution.checkpoints.all { it.type == "slot" }, "Slot checkpoints")
    }

    @Test
    fun `A qualified slot is named by its environment and its qualifier`() {
        val demo = SlotTestFixtures.testSlot(env = staging, project = project, qualifier = "demo")
        every { slotService.findSlotsByProject(project) } returns setOf(demo)
        assertEquals("staging [demo]", contributor.contribute(main).checkpoints.single().name)
    }

    @Test
    fun `A project with no slot contributes nothing`() {
        every { slotService.findSlotsByProject(project) } returns emptySet()
        assertEquals(emptyList(), contributor.contribute(main).checkpoints)
        assertEquals(emptyList(), contributor.contribute(main).edges)
    }

    @Test
    fun `The slot names the most recently deployed build`() {
        deployed(stagingSlot, BuildFixtures.testBuild(branch = main, name = "105"))
        val checkpoint = contributor.contribute(main).checkpoints.first { it.name == "staging" }
        assertEquals("105", checkpoint.arrival?.build?.name)
        assertNull(checkpoint.arrival?.status, "Deploying is the whole outcome")
    }

    @Test
    fun `A slot nothing has been deployed to names no build`() {
        assertNull(contributor.contribute(main).checkpoints.first().arrival)
    }

    @Test
    fun `A build of another branch is marked as such`() {
        // The deliberate exception to ADR 0007, recorded in ADR 0009
        deployed(stagingSlot, BuildFixtures.testBuild(branch = maintenance, name = "89"))
        val checkpoint = contributor.contribute(main).checkpoints.first { it.name == "staging" }
        assertEquals("89", checkpoint.arrival?.build?.name)
        assertEquals("release-1.3", checkpoint.data.parse<SlotCheckpointData>().otherBranch)
    }

    @Test
    fun `A build of this branch is not marked as coming from another one`() {
        deployed(stagingSlot, BuildFixtures.testBuild(branch = main, name = "105"))
        val checkpoint = contributor.contribute(main).checkpoints.first { it.name == "staging" }
        assertNull(checkpoint.data.parse<SlotCheckpointData>().otherBranch)
    }

    @Test
    fun `A promotion admission rule requires the promotion level of this branch`() {
        every {
            structureService.findPromotionLevelByName("P", "main", "GOLD")
        } returns Optional.of(gold)
        rules(productionSlot, PromotionSlotAdmissionRule.ID to PromotionSlotAdmissionRuleConfig("GOLD"))

        val edge = contributor.contribute(main).edges.single()
        assertEquals(DeliveryMapEdgeKind.REQUIRES, edge.kind)
        assertEquals("promotion-level:3", edge.source)
        assertEquals("slot:${productionSlot.id}", edge.target)
    }

    @Test
    fun `A promotion admission rule naming no promotion level of this branch draws no edge`() {
        // The name is resolved per branch, so the same slot yields a different edge on every branch
        rules(productionSlot, PromotionSlotAdmissionRule.ID to PromotionSlotAdmissionRuleConfig("GOLD"))
        assertEquals(emptyList(), contributor.contribute(main).edges)
    }

    @Test
    fun `An environment admission rule requires the slot it names`() {
        rules(productionSlot, EnvironmentSlotAdmissionRule.ID to EnvironmentSlotAdmissionRuleConfig("staging"))
        val edge = contributor.contribute(main).edges.single()
        assertEquals(DeliveryMapEdgeKind.REQUIRES, edge.kind)
        assertEquals("slot:${stagingSlot.id}", edge.source)
        assertEquals("slot:${productionSlot.id}", edge.target)
    }

    @Test
    fun `An environment admission rule naming another qualifier draws no edge`() {
        rules(
            productionSlot,
            EnvironmentSlotAdmissionRule.ID to EnvironmentSlotAdmissionRuleConfig("staging", qualifier = "demo"),
        )
        assertEquals(emptyList(), contributor.contribute(main).edges)
    }

    @Test
    fun `Slots are never joined by their environment order alone`() {
        // No fallback to environment ordering: where nobody configured the rule there is no edge
        assertEquals(emptyList(), contributor.contribute(main).edges)
    }

    @Test
    fun `A branch pattern excluding this branch makes the slot unreachable`() {
        rules(
            productionSlot,
            BranchPatternSlotAdmissionRule.ID to BranchPatternSlotAdmissionRuleConfig(includes = listOf("main")),
        )
        val checkpoint = contributor.contribute(maintenance).checkpoints.first { it.name == "production" }
        assertTrue(checkpoint.data.parse<SlotCheckpointData>().unreachable, "Unreachable")
    }

    @Test
    fun `An unreachable slot shows no build`() {
        // "You cannot get there from here" is the answer; a build would argue with it
        deployed(productionSlot, BuildFixtures.testBuild(branch = main, name = "104"))
        rules(
            productionSlot,
            BranchPatternSlotAdmissionRule.ID to BranchPatternSlotAdmissionRuleConfig(includes = listOf("main")),
        )
        val checkpoint = contributor.contribute(maintenance).checkpoints.first { it.name == "production" }
        assertNull(checkpoint.arrival)
    }

    @Test
    fun `A branch pattern including this branch leaves the slot reachable`() {
        rules(
            productionSlot,
            BranchPatternSlotAdmissionRule.ID to BranchPatternSlotAdmissionRuleConfig(includes = listOf("main")),
        )
        val checkpoint = contributor.contribute(main).checkpoints.first { it.name == "production" }
        assertEquals(false, checkpoint.data.parse<SlotCheckpointData>().unreachable)
    }

    @Test
    fun `An unparseable admission rule config costs its own rule and no more`() {
        rules(
            productionSlot,
            PromotionSlotAdmissionRule.ID to mapOf("nothing" to "useful"),
        )
        val contribution = contributor.contribute(main)
        assertEquals(2, contribution.checkpoints.size, "Both slots still on the map")
        assertEquals(emptyList(), contribution.edges)
    }

    @Test
    fun `An admission rule kind the map knows nothing about is ignored`() {
        rules(productionSlot, "manualApproval" to mapOf("users" to listOf("damien")))
        assertEquals(2, contributor.contribute(main).checkpoints.size)
        assertEquals(emptyList(), contributor.contribute(main).edges)
    }

}

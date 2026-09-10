package net.nemerosa.ontrack.extension.general.deliverymap

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.general.PreviousPromotionConditionResolution
import net.nemerosa.ontrack.extension.general.PreviousPromotionConditionService
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapEdgeKind
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PreviousPromotionConditionDeliveryMapContributorTest {

    private val branch = Branch.of(
        Project.of(nd("P", "")).withId(ID.of(1)),
        nd("B", "")
    ).withId(ID.of(1))

    private val bronze = PromotionLevel.of(branch, nd("BRONZE", "")).withId(ID.of(1))
    private val silver = PromotionLevel.of(branch, nd("SILVER", "")).withId(ID.of(2))
    private val gold = PromotionLevel.of(branch, nd("GOLD", "")).withId(ID.of(3))

    private lateinit var previousPromotionConditionService: PreviousPromotionConditionService
    private lateinit var contributor: PreviousPromotionConditionDeliveryMapContributor

    /**
     * What the service would answer for each promotion level, defaulting to *not required*. Held as
     * state rather than stubbed per call because the contributor asks for the whole branch at once.
     */
    private val resolutions = mutableMapOf<ID, PreviousPromotionConditionResolution>()

    @BeforeEach
    fun setup() {
        previousPromotionConditionService = mockk()
        every { previousPromotionConditionService.resolvePreviousPromotionConditions(any()) } answers {
            firstArg<List<PromotionLevel>>().associate { promotionLevel ->
                promotionLevel.id to (
                        resolutions[promotionLevel.id]
                            ?: PreviousPromotionConditionResolution(required = false, source = null)
                        )
            }
        }
        contributor = PreviousPromotionConditionDeliveryMapContributor(
            structureService = mockk<StructureService>().apply {
                every { getPromotionLevelListForBranch(branch.id) } returns listOf(bronze, silver, gold)
            },
            previousPromotionConditionService = previousPromotionConditionService,
        )
    }

    private fun required(promotionLevel: PromotionLevel, source: ProjectEntity?) {
        resolutions[promotionLevel.id] = PreviousPromotionConditionResolution(required = true, source = source)
    }

    @Test
    fun `The condition requires the immediate predecessor, running from it`() {
        required(silver, silver)
        val edge = contributor.contribute(branch).edges.single()
        assertEquals(DeliveryMapEdgeKind.REQUIRES, edge.kind)
        assertEquals("promotion-level:1", edge.source)
        assertEquals("promotion-level:2", edge.target)
    }

    @Test
    fun `The condition contributes no checkpoint`() {
        required(silver, silver)
        assertEquals(emptyList(), contributor.contribute(branch).checkpoints)
    }

    @Test
    fun `Nothing is drawn where the condition resolves to false`() {
        assertEquals(emptyList(), contributor.contribute(branch).edges)
    }

    @Test
    fun `The first promotion level of the order has no predecessor and draws nothing`() {
        required(bronze, bronze)
        assertEquals(emptyList(), contributor.contribute(branch).edges)
    }

    @Test
    fun `A condition inherited from the branch draws the same edge as one set on the promotion level`() {
        // The map draws the RESOLVED truth: where the constraint was configured is not its subject
        required(silver, branch)
        val edge = contributor.contribute(branch).edges.single()
        assertEquals("promotion-level:1", edge.source)
        assertEquals("promotion-level:2", edge.target)
    }

    @Test
    fun `A condition inherited from the global settings draws its edges too`() {
        required(silver, null)
        required(gold, null)
        assertEquals(
            listOf("promotion-level:1" to "promotion-level:2", "promotion-level:2" to "promotion-level:3"),
            contributor.contribute(branch).edges.map { it.source to it.target },
        )
    }

    @Test
    fun `At most one edge per promotion level - a chain, never a mesh`() {
        required(silver, null)
        required(gold, null)
        // n promotion levels, at most n-1 edges
        assertEquals(2, contributor.contribute(branch).edges.size)
    }

    @Test
    fun `A promotion level overriding the condition to false breaks the chain`() {
        required(silver, null)
        // GOLD keeps the default `false` of the setup
        assertEquals(
            listOf("promotion-level:1" to "promotion-level:2"),
            contributor.contribute(branch).edges.map { it.source to it.target },
        )
    }

    @Test
    fun `The whole branch is resolved in one call`() {
        // One call per map rather than one per promotion level: see the service's own test for why
        contributor.contribute(branch)
        verify(exactly = 1) {
            previousPromotionConditionService.resolvePreviousPromotionConditions(listOf(bronze, silver, gold))
        }
    }

    @Test
    fun `A branch with no promotion level at all draws nothing`() {
        val empty = Branch.of(branch.project, nd("empty", "")).withId(ID.of(2))
        val contributor = PreviousPromotionConditionDeliveryMapContributor(
            structureService = mockk<StructureService>().apply {
                every { getPromotionLevelListForBranch(empty.id) } returns emptyList()
            },
            previousPromotionConditionService = previousPromotionConditionService,
        )
        assertEquals(emptyList(), contributor.contribute(empty).edges)
    }

}

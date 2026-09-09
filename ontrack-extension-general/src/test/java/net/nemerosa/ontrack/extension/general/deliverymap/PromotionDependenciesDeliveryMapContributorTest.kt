package net.nemerosa.ontrack.extension.general.deliverymap

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.general.PromotionDependenciesProperty
import net.nemerosa.ontrack.extension.general.PromotionDependenciesPropertyType
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapEdgeKind
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PromotionDependenciesDeliveryMapContributorTest {

    private val branch = Branch.of(
        Project.of(nd("P", "")).withId(ID.of(1)),
        nd("B", "")
    ).withId(ID.of(1))

    private val bronze = PromotionLevel.of(branch, nd("BRONZE", "")).withId(ID.of(1))
    private val silver = PromotionLevel.of(branch, nd("SILVER", "")).withId(ID.of(2))
    private val gold = PromotionLevel.of(branch, nd("GOLD", "")).withId(ID.of(3))

    private lateinit var propertyService: PropertyService
    private lateinit var contributor: PromotionDependenciesDeliveryMapContributor

    @BeforeEach
    fun setup() {
        propertyService = mockk()
        every { propertyService.getPropertyValue(any(), PromotionDependenciesPropertyType::class.java) } returns null
        contributor = PromotionDependenciesDeliveryMapContributor(
            structureService = mockk<StructureService>().apply {
                every { getPromotionLevelListForBranch(branch.id) } returns listOf(bronze, silver, gold)
            },
            propertyService = propertyService,
        )
    }

    private fun dependencies(promotionLevel: PromotionLevel, vararg names: String) {
        every {
            propertyService.getPropertyValue(promotionLevel, PromotionDependenciesPropertyType::class.java)
        } returns PromotionDependenciesProperty(dependencies = names.toList())
    }

    @Test
    fun `A promotion dependency requires its dependency, running from the prerequisite`() {
        dependencies(silver, "BRONZE")
        val edge = contributor.contribute(branch).edges.single()
        assertEquals(DeliveryMapEdgeKind.REQUIRES, edge.kind)
        assertEquals("promotion-level:1", edge.source)
        assertEquals("promotion-level:2", edge.target)
    }

    @Test
    fun `Promotion dependencies contribute no checkpoint`() {
        // Both ends are promotion levels, which the core puts on the map already
        dependencies(silver, "BRONZE")
        assertEquals(emptyList(), contributor.contribute(branch).checkpoints)
    }

    @Test
    fun `A dependency naming no promotion level of the branch draws no edge`() {
        // The property names its dependencies rather than referencing them; #1705 surfaces this
        dependencies(silver, "PLATINUM")
        assertEquals(emptyList(), contributor.contribute(branch).edges)
    }

    @Test
    fun `Every dependency of every promotion level draws its own edge`() {
        dependencies(silver, "BRONZE", "GOLD")
        dependencies(gold, "BRONZE")
        assertEquals(
            listOf(
                "requires:promotion-level:1->promotion-level:2",
                "requires:promotion-level:3->promotion-level:2",
                "requires:promotion-level:1->promotion-level:3",
            ),
            contributor.contribute(branch).edges.map { it.id },
        )
    }

    @Test
    fun `No promotion dependency anywhere contributes nothing`() {
        val contribution = contributor.contribute(branch)
        assertEquals(emptyList(), contribution.checkpoints)
        assertEquals(emptyList(), contribution.edges)
    }

}

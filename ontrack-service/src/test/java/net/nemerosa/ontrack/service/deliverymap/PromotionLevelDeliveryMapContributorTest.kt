package net.nemerosa.ontrack.service.deliverymap

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapCheckpoint
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapCheckpointFactory
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapCheckpointTypes
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PromotionLevelDeliveryMapContributorTest {

    private val branch = Branch.of(
        Project.of(nd("P", "")).withId(ID.of(1)),
        nd("B", "")
    ).withId(ID.of(1))

    private val bronze = PromotionLevel.of(branch, nd("BRONZE", "")).withId(ID.of(1))
    private val silver = PromotionLevel.of(branch, nd("SILVER", "")).withId(ID.of(2))

    private fun contribute(promotionLevels: List<PromotionLevel>) =
        PromotionLevelDeliveryMapContributor(
            structureService = mockk<StructureService>().apply {
                every { getPromotionLevelListForBranch(branch.id) } returns promotionLevels
            },
            checkpointFactory = mockk<DeliveryMapCheckpointFactory>().apply {
                every { promotionLevel(any()) } answers {
                    val pl = firstArg<PromotionLevel>()
                    DeliveryMapCheckpoint(
                        id = DeliveryMapCheckpointTypes.promotionLevel(pl.id),
                        type = DeliveryMapCheckpointTypes.PROMOTION_LEVEL,
                        name = pl.name,
                    )
                }
            },
        ).contribute(branch)

    @Test
    fun `Every promotion level of the branch is on the map, in the branch's own order`() {
        val contribution = contribute(listOf(bronze, silver))
        assertEquals(listOf("promotion-level:1", "promotion-level:2"), contribution.checkpoints.map { it.id })
    }

    @Test
    fun `A promotion level nothing points at is still on the map`() {
        // Unlike a validation stamp: a promotion level with no configuration yet is exactly what the
        // map is there to make visible
        assertEquals(listOf("BRONZE"), contribute(listOf(bronze)).checkpoints.map { it.name })
    }

    @Test
    fun `Promotion levels carry no edges of their own`() {
        // Both of the properties which make promotion levels depend on each other are extension-owned
        assertEquals(emptyList(), contribute(listOf(bronze, silver)).edges)
    }

    @Test
    fun `A branch with no promotion level contributes nothing`() {
        val contribution = contribute(emptyList())
        assertEquals(emptyList(), contribution.checkpoints)
        assertEquals(emptyList(), contribution.edges)
    }

}

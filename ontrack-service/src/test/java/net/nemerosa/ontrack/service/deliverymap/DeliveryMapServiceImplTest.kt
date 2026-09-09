package net.nemerosa.ontrack.service.deliverymap

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.model.deliverymap.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeliveryMapServiceImplTest {

    private val branch = Branch.of(
        Project.of(nd("P", "")).withId(ID.of(1)),
        nd("B", "")
    ).withId(ID.of(1))

    private fun checkpoint(id: String) = DeliveryMapCheckpoint(
        id = id,
        type = "test",
        name = id,
    )

    private fun edge(source: String, target: String) = DeliveryMapEdge(
        id = "$source->$target",
        kind = DeliveryMapEdgeKind.UNLOCKS,
        source = source,
        target = target,
    )

    private fun contributor(contribution: DeliveryMapContribution): DeliveryMapContributor =
        mockk<DeliveryMapContributor>().apply {
            every { contribute(branch) } returns contribution
        }

    private fun failing(): DeliveryMapContributor =
        mockk<DeliveryMapContributor>().apply {
            every { contribute(branch) } throws RuntimeException("Contributor is broken")
        }

    private fun map(vararg contributors: DeliveryMapContributor) =
        DeliveryMapServiceImpl(
            contributors = contributors.toList(),
            securityService = MockSecurityService(),
        ).getDeliveryMap(branch)

    @Test
    fun `Assembles the checkpoints and the edges of every contributor`() {
        val map = map(
            contributor(
                DeliveryMapContribution(
                    checkpoints = listOf(checkpoint("a"), checkpoint("b")),
                    edges = listOf(edge("a", "b")),
                )
            ),
            contributor(
                DeliveryMapContribution(
                    checkpoints = listOf(checkpoint("c")),
                    edges = listOf(edge("b", "c")),
                )
            ),
        )
        assertEquals(listOf("a", "b", "c"), map.checkpoints.map { it.id })
        assertEquals(listOf("a->b", "b->c"), map.edges.map { it.id })
    }

    @Test
    fun `An edge may join two checkpoints contributed by two different contributors`() {
        // The whole point of the seam: a slot admission rule naming a promotion level
        val map = map(
            contributor(DeliveryMapContribution(checkpoints = listOf(checkpoint("a")))),
            contributor(
                DeliveryMapContribution(
                    checkpoints = listOf(checkpoint("b")),
                    edges = listOf(edge("a", "b")),
                )
            ),
        )
        assertEquals(listOf("a->b"), map.edges.map { it.id })
    }

    @Test
    fun `A broken contributor loses its own contribution, not the whole map`() {
        val map = map(
            failing(),
            contributor(
                DeliveryMapContribution(
                    checkpoints = listOf(checkpoint("a"), checkpoint("b")),
                    edges = listOf(edge("a", "b")),
                )
            ),
        )
        assertEquals(listOf("a", "b"), map.checkpoints.map { it.id })
        assertEquals(listOf("a->b"), map.edges.map { it.id })
    }

    @Test
    fun `An edge whose source is not on the map is dropped`() {
        // A checkpoint the user may not see is left out by its contributor, and this is what then
        // removes its edges with it - never leaving a dangling edge to nowhere on screen
        val map = map(
            contributor(
                DeliveryMapContribution(
                    checkpoints = listOf(checkpoint("b")),
                    edges = listOf(edge("a", "b")),
                )
            ),
        )
        assertEquals(listOf("b"), map.checkpoints.map { it.id })
        assertEquals(emptyList(), map.edges.map { it.id })
    }

    @Test
    fun `An edge whose target is not on the map is dropped`() {
        val map = map(
            contributor(
                DeliveryMapContribution(
                    checkpoints = listOf(checkpoint("a")),
                    edges = listOf(edge("a", "b")),
                )
            ),
        )
        assertEquals(listOf("a"), map.checkpoints.map { it.id })
        assertEquals(emptyList(), map.edges.map { it.id })
    }

    @Test
    fun `A checkpoint contributed twice appears once`() {
        // React Flow needs unique node ids, and two contributors reaching the same promotion level
        // must not remount it
        val map = map(
            contributor(DeliveryMapContribution(checkpoints = listOf(checkpoint("a")))),
            contributor(DeliveryMapContribution(checkpoints = listOf(checkpoint("a")))),
        )
        assertEquals(listOf("a"), map.checkpoints.map { it.id })
    }

    @Test
    fun `An edge contributed twice appears once`() {
        val map = map(
            contributor(
                DeliveryMapContribution(
                    checkpoints = listOf(checkpoint("a"), checkpoint("b")),
                    edges = listOf(edge("a", "b")),
                )
            ),
            contributor(DeliveryMapContribution(edges = listOf(edge("a", "b")))),
        )
        assertEquals(listOf("a->b"), map.edges.map { it.id })
    }

    @Test
    fun `No contributor at all gives an empty map`() {
        val map = map()
        assertEquals(DeliveryMap.EMPTY, map)
    }

}

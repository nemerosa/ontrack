package net.nemerosa.ontrack.service.deliverymap

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.model.deliverymap.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeliveryMapServiceImplTest {

    private val project = Project.of(nd("P", "")).withId(ID.of(1))

    private val branch = Branch.of(project, nd("B", "")).withId(ID.of(1))

    private val otherBranch = Branch.of(project, nd("other", "")).withId(ID.of(2))

    private val structureService = mockk<StructureService>(relaxed = true)

    private fun build(id: Int, on: Branch = branch) =
        Build.of(on, nd("$id", ""), Signature.of("test")).withId(ID.of(id))

    private fun arrived(id: String, build: Build) = DeliveryMapCheckpoint(
        id = id,
        type = "test",
        name = id,
        arrival = DeliveryMapArrival(build = build, time = LocalDateTime.now()),
    )

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
            structureService = structureService,
        ).getDeliveryMap(branch)

    /**
     * A branch whose latest build is [head], and where every other build of it is [newer] builds
     * behind that head.
     */
    private fun branchAt(head: Build?, newer: Int = 0) {
        every { structureService.getLastBuildForBranch(branch) } returns head
        every { structureService.getNewerBuildCount(any()) } returns newer
    }

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
        branchAt(null)
        val map = map()
        assertEquals(DeliveryMap.EMPTY, map)
    }

    @Test
    fun `The map carries the branch's latest build as its head`() {
        // Not a checkpoint and not an edge: the header states it once, and each checkpoint states
        // its own lag from it
        val head = build(47)
        branchAt(head)
        val map = map(contributor(DeliveryMapContribution(checkpoints = listOf(checkpoint("a")))))
        assertEquals(head, map.head)
    }

    @Test
    fun `A checkpoint the head has reached is at the head`() {
        val head = build(47)
        branchAt(head)
        val map = map(contributor(DeliveryMapContribution(checkpoints = listOf(arrived("a", head)))))
        assertEquals(0, map.checkpoints.first().arrival?.lag)
    }

    @Test
    fun `A checkpoint naming an older build says how far behind it is`() {
        branchAt(build(47), newer = 5)
        val map = map(contributor(DeliveryMapContribution(checkpoints = listOf(arrived("a", build(42))))))
        assertEquals(5, map.checkpoints.first().arrival?.lag)
    }

    @Test
    fun `A checkpoint naming another branch's build has no lag at all`() {
        // ADR 0009: a slot names what is deployed in it, whatever branch that build belongs to.
        // Counting it against this branch's head would answer a question nobody asked.
        branchAt(build(47), newer = 5)
        val foreign = build(30, on = otherBranch)
        val map = map(contributor(DeliveryMapContribution(checkpoints = listOf(arrived("a", foreign)))))
        assertNull(map.checkpoints.first().arrival?.lag)
    }

    @Test
    fun `A branch with no build at all leaves every lag unknown`() {
        branchAt(null)
        val map = map(contributor(DeliveryMapContribution(checkpoints = listOf(arrived("a", build(42))))))
        assertNull(map.checkpoints.first().arrival?.lag)
    }

    @Test
    fun `A checkpoint nothing has reached keeps no arrival`() {
        branchAt(build(47))
        val map = map(contributor(DeliveryMapContribution(checkpoints = listOf(checkpoint("a")))))
        assertNull(map.checkpoints.first().arrival)
    }

    @Test
    fun `The members of an aggregate get their lag as well`() {
        // An aggregate shows its members' builds, so a member with no lag would be the one place on
        // the map where a build is named without saying how old it is
        branchAt(build(47), newer = 5)
        val aggregate = DeliveryMapCheckpoint(
            id = "pattern",
            type = "test",
            name = "pattern",
            members = listOf(arrived("a", build(42))),
        )
        val map = map(contributor(DeliveryMapContribution(checkpoints = listOf(aggregate))))
        assertEquals(5, map.checkpoints.first().members.first().arrival?.lag)
    }

    @Test
    fun `One build is counted once, however many checkpoints name it`() {
        // Every promotion level of a branch routinely names the same build
        branchAt(build(47), newer = 5)
        val older = build(42)
        map(
            contributor(
                DeliveryMapContribution(
                    checkpoints = listOf(arrived("a", older), arrived("b", older)),
                )
            )
        )
        verify(exactly = 1) { structureService.getNewerBuildCount(any()) }
    }

    @Test
    fun `The head itself is not counted against the database`() {
        val head = build(47)
        branchAt(head)
        map(contributor(DeliveryMapContribution(checkpoints = listOf(arrived("a", head)))))
        verify(exactly = 0) { structureService.getNewerBuildCount(any()) }
    }

}

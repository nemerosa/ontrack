package net.nemerosa.ontrack.service.ordering

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.ordering.BranchOrderingService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Integration tests for the ordering of branches.
 */
class BranchOrderingIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var branchOrderingService: BranchOrderingService

    @Test
    fun `Ordering by ID`() {
        project {
            val b1 = branch()
            val b2 = branch()
            val b3 = branch()
            val ordering = branchOrderingService.getBranchOrdering("id")
            assertNotNull(ordering) { order ->
                val comparator = order.getComparator(null) // No parameter needed
                val branches = listOf(b1, b2, b3).sortedWith(comparator)
                assertEquals(
                        listOf(b3, b2, b1).map { it.id() },
                        branches.map { it.id() }
                )
            }
        }
    }

    @Test
    fun `Ordering by version with only matching versions`() {
        project {
            val b1 = branch("release-1.0")
            val b2 = branch("release-2.0")
            val b3 = branch("release-1.1")
            val ordering = branchOrderingService.getBranchOrdering("version")
            assertNotNull(ordering) { order ->
                val comparator = order.getComparator("release-(\\d+\\.\\d+)") // No parameter needed
                val branches = listOf(b1, b2, b3).sortedWith(comparator)
                assertEquals(
                        listOf(b2, b3, b1).map { it.id() },
                        branches.map { it.id() }
                )
            }
        }
    }

}
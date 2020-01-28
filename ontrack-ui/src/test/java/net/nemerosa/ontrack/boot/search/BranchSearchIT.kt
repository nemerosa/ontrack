package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.SearchRequest
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Search indexation for branches
 */
class BranchSearchIT : AbstractSearchTestSupport() {

    @Test
    fun `Indexation of branches and looking for branches`() {
        val candidate = project<Branch> {
            branch()
        }
        // Creates 3 other projects
        repeat(3) { project { branch {} } }
        // Launching indexation for the projects & branches
        index("projects")
        index("branches")
        // Searches for the candidate project
        val results = searchService.search(SearchRequest(candidate.name))
        assertEquals(1, results.size)
        val result = results.first()
        result.apply {
            assertEquals(candidate.name, result.title)
            assertEquals(candidate.entityDisplayName, result.description)
        }
    }

}
package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.SearchRequest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun `Finding projects and branches`() {
        // Creates a project
        val project = project()
        // Creates a branch with the same name than the project above
        val branch = project<Branch> {
            branch(name = project.name)
        }
        // Creates 3 other projects
        repeat(3) { project { branch {} } }
        // Launching indexation for the projects & branches
        index("projects")
        index("branches")
        // Searches for the name
        val results = searchService.search(SearchRequest(branch.name)).toList()
        assertEquals(2, results.size)
        assertTrue(results[0].accuracy > results[1].accuracy, "Project is returned first")
        results[0].apply {
            assertEquals(project.name, title)
            assertEquals(project.entityDisplayName, description)
        }
        results[1].apply {
            assertEquals(branch.name, title)
            assertEquals(branch.entityDisplayName, description)
        }
    }

}
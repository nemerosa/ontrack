package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.boot.BRANCH_SEARCH_INDEX
import net.nemerosa.ontrack.boot.PROJECT_SEARCH_INDEX
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.test.TestUtils.uid
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
        val results = asUser { searchService.paginatedSearch(SearchRequest(candidate.name)).items }
        assertEquals(1, results.size)
        val result = results.first()
        result.apply {
            assertEquals(candidate.entityDisplayName, result.title)
            assertEquals(candidate.description, result.description)
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
        index(PROJECT_SEARCH_INDEX)
        index("branches")
        // Searches for the name
        val results = asUser { searchService.paginatedSearch(SearchRequest(branch.name)).items }
        assertEquals(2, results.size)
        assertTrue(results[0].accuracy > results[1].accuracy, "Project is returned first")
        results[0].apply {
            assertEquals(project.entityDisplayName, title)
            assertEquals(project.description, description)
        }
        results[1].apply {
            assertEquals(branch.entityDisplayName, title)
            assertEquals(branch.description, description)
        }
    }

    @Test
    fun `Search branch on project name`() {
        val branch = project<Branch> {
            branch {}
        }
        // Launching indexation for the projects
        index(PROJECT_SEARCH_INDEX)
        index(BRANCH_SEARCH_INDEX)
        // Search on project name
        val results = asUser { searchService.paginatedSearch(SearchRequest(branch.project.name)).items }
        // Project is found
        val projectResult = results.find { it.title == branch.project.entityDisplayName }
                ?: error("Cannot find project")
        // Branch is found
        val branchResult = results.find { it.title == branch.entityDisplayName } ?: error("Cannot find branch")
        // Project result has a higher score than the branch
        assertTrue(projectResult.accuracy >= branchResult.accuracy, "Project result has a higher score than the branch")
    }

    @Test
    fun `Search branches and filter on access rights`() {
        val prefix = uid("P")
        // Creates projects and branches
        val branches = (0..3).map {
            project<Branch> {
                branch(name = "$prefix-$it")
            }
        }
        // Launching indexation for the projects
        index(PROJECT_SEARCH_INDEX)
        index(BRANCH_SEARCH_INDEX)
        // Making sure to restrict access rights
        withNoGrantViewToAll {
            // Performing a search using the prefix and being authorised only for the first branch
            branches[0].asUserWithView {
                // Launching the search
                val results = searchService.paginatedSearch(SearchRequest(prefix)).items
                // Names of branches
                val foundNames = results.map { it.title }
                // Checks that authorized branch is found
                assertTrue(branches[0].entityDisplayName in foundNames, "Authorized branch must be found")
                // Checks that unauthorized branches are NOT found
                (1..3).forEach {
                    assertTrue(branches[it].entityDisplayName !in foundNames, "Not authorized branches must be filtered out")
                }
            }
        }
    }

}
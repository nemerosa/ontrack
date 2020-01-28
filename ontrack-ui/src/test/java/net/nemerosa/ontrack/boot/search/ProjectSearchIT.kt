package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.boot.PROJECT_SEARCH_INDEX
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Search indexation for projects
 */
class ProjectSearchIT : AbstractSearchTestSupport() {

    @Test
    fun `Indexation of projects and looking for projects`() {
        val candidate = project {}
        // Creates 3 other projects
        repeat(3) { project {} }
        // Launching indexation for the projects
        index(PROJECT_SEARCH_INDEX)
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
    fun `Search projects and filter on access rights`() {
        val prefix = uid("P")
        // Creates projects
        val projects = (0..3).map {
            doCreateProject(NameDescription(prefix + it, "Project $prefix #$it"))
        }
        // Launching indexation for the projects
        index(PROJECT_SEARCH_INDEX)
        // Making sure to restrict access rights
        withNoGrantViewToAll {
            // Performing a search using the prefix and being authorised only for the first project
            projects[0].asUserWithView {
                // Launching the search
                val results = searchService.search(SearchRequest(prefix))
                // Names of projects
                val foundNames = results.map { it.title }
                // Checks that authorized project is found
                assertTrue(projects[0].name in foundNames, "Authorized project must be found")
                // Checks that unauthorized projects are NOT found
                (1..3).forEach {
                    assertTrue(projects[it].name !in foundNames, "Not authorized projects must be filtered out")
                }
            }
        }
    }

}
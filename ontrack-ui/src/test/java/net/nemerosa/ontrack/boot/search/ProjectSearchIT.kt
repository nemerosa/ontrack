package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.model.structure.SearchRequest
import org.junit.Test
import kotlin.test.assertEquals

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
        index("projects")
        // Searches for the candidate project
        val results = searchService.search(SearchRequest(candidate.name))
        assertEquals(1, results.size)
        val result = results.first()
        result.apply {
            assertEquals(candidate.name, result.title)
            assertEquals(candidate.name, result.description)
        }
    }

}
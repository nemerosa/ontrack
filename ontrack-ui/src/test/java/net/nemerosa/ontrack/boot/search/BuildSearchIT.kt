package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.boot.BUILD_SEARCH_INDEX
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertTrue

class BuildSearchIT : AbstractSearchTestSupport() {

    @Test
    fun `Searching builds`() {
        // Creates two builds with the same name
        val name = uid("B")
        val builds = (1..2).map {
            project<Build> {
                branch<Build> {
                    build(name)
                }
            }
        }
        // Creates other builds
        repeat(3) { project { branch { build() } } }
        // Indexes the builds
        index(BUILD_SEARCH_INDEX)
        // Searches for the builds using the name
        val results = asUser { searchService.paginatedSearch(SearchRequest(name)).items }
        // Checks the builds have been found
        builds.forEach { build ->
            assertTrue(build.entityDisplayName in results.map { it.title })
        }
    }

}
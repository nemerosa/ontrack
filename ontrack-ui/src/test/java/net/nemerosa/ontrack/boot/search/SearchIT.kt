package net.nemerosa.ontrack.boot.search

import org.junit.Test
import kotlin.test.assertEquals

class SearchIT : AbstractSearchTestSupport() {

    @Test
    fun `List of result types`() {
        val types = searchService.searchResultTypes
        val names = types.map { it.name }
        assertEquals(
                sortedSetOf(
                        "Project", "Branch", "Build",
                        "Git Commit", "Git Branch", "Git Issue",
                        "Build with Release", "Build with Meta Info", "Linked Build",
                        "SCM Catalog"
                ).toList(),
                names
        )
    }

}
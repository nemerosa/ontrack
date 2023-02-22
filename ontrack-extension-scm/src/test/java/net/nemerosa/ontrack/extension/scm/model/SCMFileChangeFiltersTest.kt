package net.nemerosa.ontrack.extension.scm.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SCMFileChangeFiltersTest {

    @Test
    fun `Adding a filter to an empty list`() {
        val filters = SCMFileChangeFilters.create().save(
            SCMFileChangeFilter(
                "SQL",
                listOf("**/*.sql")
            )
        )
        assertEquals(listOf("SQL"), filters.filters.map { it.name })
    }

    @Test
    fun `Adding a new filter`() {
        val filters = SCMFileChangeFilters.create().save(
            SCMFileChangeFilter(
                "SQL",
                listOf("**/*.sql")
            )
        ).save(
            SCMFileChangeFilter(
                "Build",
                listOf("**/*.gradle")
            )
        )
        assertEquals(listOf("Build", "SQL"), filters.filters.map { it.name })
    }

    @Test
    fun `Adding an existing filter`() {
        val filters = SCMFileChangeFilters.create().save(
            SCMFileChangeFilter(
                "SQL",
                listOf("**/*.sql")
            )
        ).save(
            SCMFileChangeFilter(
                "SQL",
                listOf("**/*.sql", "**/*.ddl")
            )
        )
        assertEquals(1, filters.filters.size)
        assertEquals("SQL", filters.filters.first().name)
        assertEquals(listOf("**/*.sql", "**/*.ddl"), filters.filters.first().patterns)
    }

    @Test
    fun `Removing a filter from an empty list`() {
        val filters = SCMFileChangeFilters.create().remove("SQL")
        assertEquals(emptyList(), filters.filters.map { it.name })
    }

    @Test
    fun `Removing a filter from a list`() {
        val filters = SCMFileChangeFilters.create().save(
            SCMFileChangeFilter(
                "SQL",
                listOf("**/*.sql")
            )
        ).save(
            SCMFileChangeFilter(
                "Build",
                listOf("**/*.gradle")
            )
        ).remove("SQL")
        assertEquals(listOf("Build"), filters.filters.map { it.name })
    }

}

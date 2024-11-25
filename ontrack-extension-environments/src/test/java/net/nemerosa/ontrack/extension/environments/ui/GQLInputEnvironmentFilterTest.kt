package net.nemerosa.ontrack.extension.environments.ui

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLInputEnvironmentFilterTest {

    @Test
    fun `Converting tags and projects`() {
        val input = mapOf(
            "tags" to listOf("tag1", "tag2"),
            "projects" to listOf("ontrack")
        )
        val gql = GQLInputEnvironmentFilter()
        val filter = gql.convert(input)
        assertNotNull(filter) {
            assertEquals(listOf("tag1", "tag2"), it.tags)
            assertEquals(listOf("ontrack"), it.projects)
        }
    }

    @Test
    fun `Converting tags`() {
        val input = mapOf(
            "tags" to listOf("tag1", "tag2"),
        )
        val gql = GQLInputEnvironmentFilter()
        val filter = gql.convert(input)
        assertNotNull(filter) {
            assertEquals(listOf("tag1", "tag2"), it.tags)
            assertEquals(emptyList(), it.projects)
        }
    }

    @Test
    fun `Converting projects`() {
        val input = mapOf(
            "projects" to listOf("ontrack")
        )
        val gql = GQLInputEnvironmentFilter()
        val filter = gql.convert(input)
        assertNotNull(filter) {
            assertEquals(emptyList(), it.tags)
            assertEquals(listOf("ontrack"), it.projects)
        }
    }

}
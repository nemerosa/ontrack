package net.nemerosa.ontrack.extension.issues.support

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IssueServiceUtilsTest {

    @Test
    fun `Issue groups - no types, no groups`() {
        assertTrue(IssueServiceUtils.getIssueGroups(emptyList(), emptyMap()).isEmpty())
    }

    @Test
    fun `Issue groups - no types, groups`() {
        assertTrue(IssueServiceUtils.getIssueGroups(emptyList(), mapOf("Bug" to setOf("bug"))).isEmpty())
    }

    @Test
    fun `Issue groups - one group`() {
        assertEquals(setOf("Bug"), IssueServiceUtils.getIssueGroups(listOf("bug"), mapOf("Bug" to setOf("bug"))))
    }

    @Test
    fun `Issue groups - one group among many`() {
        assertEquals(
            setOf("Bugs"),
            IssueServiceUtils.getIssueGroups(
                listOf("bug"),
                mapOf(
                    "Bugs" to setOf("bug"),
                    "Features" to setOf("feature"),
                )
            )
        )
    }

    @Test
    fun `Issue groups - no group among many`() {
        assertEquals(
            emptySet(),
            IssueServiceUtils.getIssueGroups(
                listOf("other"),
                mapOf(
                    "Bugs" to setOf("bug"),
                    "Features" to setOf("feature"),
                )
            )
        )
    }

    @Test
    fun `Issue groups - one group among many, with several types`() {
        assertEquals(
            setOf("Bugs"),
            IssueServiceUtils.getIssueGroups(
                listOf("bug", "gui"),
                mapOf(
                    "Bugs" to setOf("bug"),
                    "Features" to setOf("feature"),
                )
            )
        )
    }

    @Test
    fun `Issue groups - two groups`() {
        assertEquals(
            setOf("Bugs", "Features"),
            IssueServiceUtils.getIssueGroups(
                listOf("bug", "feature"),
                mapOf(
                    "Bugs" to setOf("bug"),
                    "Features" to setOf("feature"),
                )
            )
        )
    }

}
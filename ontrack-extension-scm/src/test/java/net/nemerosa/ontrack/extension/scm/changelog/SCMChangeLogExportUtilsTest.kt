package net.nemerosa.ontrack.extension.scm.changelog

import io.mockk.mockk
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.scm.mock.MockIssue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SCMChangeLogExportUtilsTest {

    @Test
    fun `Issue groups - no types, no groups`() {
        assertTrue(getIssueGroups(emptyList(), emptyMap()).isEmpty())
    }

    @Test
    fun `Issue groups - no types, groups`() {
        assertTrue(getIssueGroups(emptyList(), mapOf("Bug" to setOf("bug"))).isEmpty())
    }

    @Test
    fun `Issue groups - one group`() {
        assertEquals(setOf("Bug"), getIssueGroups(listOf("bug"), mapOf("Bug" to setOf("bug"))))
    }

    @Test
    fun `Issue groups - one group among many`() {
        assertEquals(
            setOf("Bugs"),
            getIssueGroups(
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
            getIssueGroups(
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
            getIssueGroups(
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
            getIssueGroups(
                listOf("bug", "feature"),
                mapOf(
                    "Bugs" to setOf("bug"),
                    "Features" to setOf("feature"),
                )
            )
        )
    }

    @Test
    fun `Grouping issues - no issue`() {
        val request = SCMChangeLogExportInput()
        val groups = groupIssues(emptyList(), request) {
            emptySet()
        }
        assertTrue(groups.isEmpty())
    }

    @Test
    fun `Grouping issues - no grouping`() {
        val request = SCMChangeLogExportInput()
        val issue = mockk<Issue>()
        val groups = groupIssues(listOf(issue), request) {
            setOf("bug")
        }
        assertEquals(1, groups.size)
        assertEquals(listOf(issue), groups[""])
    }

    @Test
    fun `Grouping issues - grouping`() {
        val request = SCMChangeLogExportInput(
            grouping = "Bugs=bug|Features=feature"
        )
        val bug1 = mockk<Issue>()
        val bug2 = mockk<Issue>()
        val feature = mockk<Issue>()

        val issueTypesFn: (u: Issue) -> Set<String> = { i ->
            if (i == bug1 || i == bug2) {
                setOf("bug")
            } else {
                setOf("feature")
            }
        }

        val groups = groupIssues(
            listOf(bug1, bug2, feature),
            request,
            issueTypesFn,
        )

        assertEquals(2, groups.size)
        assertEquals(listOf(bug1, bug2), groups["Bugs"])
        assertEquals(listOf(feature), groups["Features"])
    }

    @Test
    fun `Grouping issues - grouping and pruning`() {
        val request = SCMChangeLogExportInput(
            grouping = "Bugs=bug|Features=feature"
        )
        val bug1 = mockk<Issue>()
        val bug2 = mockk<Issue>()
        // val feature = mockk<Issue>()

        val issueTypesFn: (u: Issue) -> Set<String> = { i ->
            if (i == bug1 || i == bug2) {
                setOf("bug")
            } else {
                setOf("feature")
            }
        }

        val groups = groupIssues(
            listOf(bug1, bug2),
            request,
            issueTypesFn,
        )
        assertEquals(1, groups.size)
        assertEquals(listOf(bug1, bug2), groups["Bugs"])
    }

    @Test
    fun `Grouping issues - grouping with other (default)`() {
        val request = SCMChangeLogExportInput(
            grouping = "Bugs=bug|Features=feature"
        )
        val bug1 = testIssue(id = 1, "bug")
        val bug2 = testIssue(id = 2, "bug")
        val feature = testIssue(id = 3, "feature")
        val other = testIssue(id = 4, "other")

        val issueTypesFn: (u: Issue) -> Set<String> = { i ->
            (i as MockIssue).types ?: emptySet()
        }

        val groups = groupIssues(
            listOf(bug1, bug2, feature, other),
            request,
            issueTypesFn
        )

        assertEquals(3, groups.size)
        assertEquals(listOf(bug1, bug2), groups["Bugs"])
        assertEquals(listOf(feature), groups["Features"])
        assertEquals(listOf(other), groups["Other"])
    }

    @Test
    fun `Grouping issues - grouping with other (custom)`() {
        val request = SCMChangeLogExportInput(
            grouping = "Bugs=bug|Features=feature",
            altGroup = "Unclassified"
        )
        val bug1 = mockk<Issue>()
        val bug2 = mockk<Issue>()
        val feature = mockk<Issue>()
        val other = mockk<Issue>()

        val issueTypesFn: (u: Issue) -> Set<String> = { i ->
            when (i) {
                bug1, bug2 -> setOf("bug")
                other -> setOf("other")
                else -> setOf("feature")
            }
        }

        val groups = groupIssues(
            listOf(bug1, bug2, feature, other),
            request,
            issueTypesFn
        )

        assertEquals(3, groups.size)
        assertEquals(listOf(bug1, bug2), groups["Bugs"])
        assertEquals(listOf(feature), groups["Features"])
        assertEquals(listOf(other), groups["Unclassified"])
    }

    @Test
    fun `Grouping issues - more than one group for an issue`() {
        val request = SCMChangeLogExportInput(
            grouping = "Bugs=bug|Features=feature"
        )
        val bug1 = testIssue(id = 1, "bug")
        val bug2 = testIssue(id = 2, "bug")
        val feature = testIssue(id = 3, "bug", "feature")

        val issueTypesFn: (u: Issue) -> Set<String> = { i ->
            (i as MockIssue).types ?: emptySet()
        }

        assertFailsWith<SCMChangeLogExportMoreThanOneGroupException> {
            groupIssues(listOf(bug1, bug2, feature), request, issueTypesFn)
        }
    }

    @Test
    fun `Grouping issues - grouping with exclusion`() {
        val request = SCMChangeLogExportInput(
            grouping = "Bugs=bug|Features=feature",
            exclude = "delivery",
        )
        val bug1 = mockk<Issue>()
        val bug2 = mockk<Issue>()
        val feature = mockk<Issue>()

        val issueTypesFn: (u: Issue) -> Set<String> = { i ->
            when (i) {
                bug1 -> setOf("bug")
                bug2 -> setOf("bug", "delivery")
                else -> setOf("feature")
            }
        }

        val groups = groupIssues(listOf(bug1, bug2, feature), request, issueTypesFn)
        assertEquals(2, groups.size)
        assertEquals(listOf(bug1), groups["Bugs"])
        assertEquals(listOf(feature), groups["Features"])
    }

    @Test
    fun `Grouping issues - grouping with several exclusions`() {
        val request = SCMChangeLogExportInput(
            grouping = "Bugs=bug|Features=feature",
            exclude = "delivery, design",
        )
        val bug1 = mockk<Issue>()
        val bug2 = mockk<Issue>()
        val bug3 = mockk<Issue>()
        val feature = mockk<Issue>()

        val issueTypesFn: (u: Issue) -> Set<String> = { i ->
            when (i) {
                bug1 -> setOf("bug")
                bug2 -> setOf("bug", "delivery")
                bug3 -> setOf("bug", "design")
                else -> setOf("feature")
            }
        }

        val groups = groupIssues(
            listOf(bug1, bug2, bug3, feature),
            request,
            issueTypesFn,
        )
        assertEquals(2, groups.size)
        assertEquals(listOf(bug1), groups["Bugs"])
        assertEquals(listOf(feature), groups["Features"])
    }

    private fun testIssue(id: Int, vararg types: String) = MockIssue(
        repositoryName = "not-used",
        key = "ISS-$id",
        message = "Issue #$id",
        types = types.toSet(),
    )

}
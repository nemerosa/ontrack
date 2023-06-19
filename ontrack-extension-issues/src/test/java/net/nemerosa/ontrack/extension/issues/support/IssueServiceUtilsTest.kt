package net.nemerosa.ontrack.extension.issues.support

import io.mockk.mockk
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.issues.mock.TestIssue
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueExportMoreThanOneGroupException
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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

    @Test
    fun `Grouping issues - no issue`() {
        val configuration = mockk<IssueServiceConfiguration>()
        val request = IssueChangeLogExportRequest()
        val groups = IssueServiceUtils.groupIssues(configuration, emptyList(), request) { _, _ ->
            emptySet()
        }
        assertTrue(groups.isEmpty())
    }

    @Test
    fun `Grouping issues - no grouping`() {
        val configuration = mockk<IssueServiceConfiguration>()
        val request = IssueChangeLogExportRequest()
        val issue = mockk<Issue>()
        val groups = IssueServiceUtils.groupIssues(configuration, listOf(issue), request) { _, _ ->
            setOf("bug")
        }
        assertEquals(1, groups.size)
        assertEquals(listOf(issue), groups[""])
    }

    @Test
    fun `Grouping issues - grouping`() {
        val configuration = mockk<IssueServiceConfiguration>()
        val request = IssueChangeLogExportRequest()
        request.grouping = "Bugs=bug|Features=feature"
        val bug1 = mockk<Issue>()
        val bug2 = mockk<Issue>()
        val feature = mockk<Issue>()

        val issueTypesFn: (t: IssueServiceConfiguration, u: Issue) -> Set<String> = { _, i ->
            if (i == bug1 || i == bug2) {
                setOf("bug")
            } else {
                setOf("feature")
            }
        }

        val groups = IssueServiceUtils.groupIssues(
                configuration,
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
        val configuration = mockk<IssueServiceConfiguration>()
        val request = IssueChangeLogExportRequest()
        request.grouping = "Bugs=bug|Features=feature"
        val bug1 = mockk<Issue>()
        val bug2 = mockk<Issue>()
        // val feature = mockk<Issue>()

        val issueTypesFn: (t: IssueServiceConfiguration, u: Issue) -> Set<String> = { _, i ->
            if (i == bug1 || i == bug2) {
                setOf("bug")
            } else {
                setOf("feature")
            }
        }

        val groups = IssueServiceUtils.groupIssues(
                configuration,
                listOf(bug1, bug2),
                request,
                issueTypesFn,
        )
        assertEquals(1, groups.size)
        assertEquals(listOf(bug1, bug2), groups["Bugs"])
    }

    @Test
    fun `Grouping issues - grouping with other (default)`() {
        val configuration = mockk<IssueServiceConfiguration>()
        val request = IssueChangeLogExportRequest()
        request.grouping = "Bugs=bug|Features=feature"
        val bug1 = mockk<Issue>()
        val bug2 = mockk<Issue>()
        val feature = mockk<Issue>()
        val other = mockk<Issue>()

        val issueTypesFn: (t: IssueServiceConfiguration, u: Issue) -> Set<String> = { _, i ->
            when (i) {
                bug1, bug2 -> setOf("bug")
                other -> setOf("other")
                else -> setOf("feature")
            }
        }

        val groups = IssueServiceUtils.groupIssues(
                configuration,
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
        val configuration = mockk<IssueServiceConfiguration>()
        val request = IssueChangeLogExportRequest()
        request.grouping = "Bugs=bug|Features=feature"
        request.altGroup = "Unclassified"
        val bug1 = TestIssue(1)
        val bug2 = TestIssue(2)
        val feature = TestIssue(3)
        val other = TestIssue(4)

        val issueTypesFn: (t: IssueServiceConfiguration, u: Issue) -> Set<String> = { _, i ->
            when (i) {
                bug1, bug2 -> setOf("bug")
                other -> setOf("other")
                else -> setOf("feature")
            }
        }

        val groups = IssueServiceUtils.groupIssues(
                configuration,
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
        val configuration = mockk<IssueServiceConfiguration>()
        val request = IssueChangeLogExportRequest()
        request.grouping = "Bugs=bug|Features=feature"
        val bug1 = TestIssue(1)
        val bug2 = TestIssue(2)
        val feature = TestIssue(3)

        val issueTypesFn: (t: IssueServiceConfiguration, u: Issue) -> Set<String> = { _, i ->
            when (i) {
                bug1, bug2 -> setOf("bug")
                else -> setOf("feature", "bug")
            }
        }

        assertFailsWith<IssueExportMoreThanOneGroupException> {
            IssueServiceUtils.groupIssues(configuration, listOf(bug1, bug2, feature), request, issueTypesFn)
        }
    }

    @Test
    fun `Grouping issues - grouping with exclusion`() {
        val configuration = mockk<IssueServiceConfiguration>()
        val request = IssueChangeLogExportRequest()
        request.grouping = "Bugs=bug|Features=feature"
        request.exclude = "delivery"
        val bug1 = mockk<Issue>()
        val bug2 = mockk<Issue>()
        val feature = mockk<Issue>()

        val issueTypesFn: (t: IssueServiceConfiguration, u: Issue) -> Set<String> = { _, i ->
            when (i) {
                bug1 -> setOf("bug")
                bug2 -> setOf("bug", "delivery")
                else -> setOf("feature")
            }
        }

        val groups = IssueServiceUtils.groupIssues(configuration, listOf(bug1, bug2, feature), request, issueTypesFn)
        assertEquals(2, groups.size)
        assertEquals(listOf(bug1), groups["Bugs"])
        assertEquals(listOf(feature), groups["Features"])
    }

    @Test
    fun `Grouping issues - grouping with several exclusions`() {
        val configuration = mockk<IssueServiceConfiguration>()
        val request = IssueChangeLogExportRequest()
        request.grouping = "Bugs=bug|Features=feature"
        request.exclude = "delivery, design"
        val bug1 = mockk<Issue>()
        val bug2 = mockk<Issue>()
        val bug3 = mockk<Issue>()
        val feature = mockk<Issue>()

        val issueTypesFn: (t: IssueServiceConfiguration, u: Issue) -> Set<String> = { _, i ->
            when (i) {
                bug1 -> setOf("bug")
                bug2 -> setOf("bug", "delivery")
                bug3 -> setOf("bug", "design")
                else -> setOf("feature")
            }
        }

        val groups = IssueServiceUtils.groupIssues(
                configuration,
                listOf(bug1, bug2, bug3, feature),
                request,
                issueTypesFn,
        )
        assertEquals(2, groups.size)
        assertEquals(listOf(bug1), groups["Bugs"])
        assertEquals(listOf(feature), groups["Features"])
    }

}
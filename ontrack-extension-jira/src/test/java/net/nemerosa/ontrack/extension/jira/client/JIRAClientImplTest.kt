package net.nemerosa.ontrack.extension.jira.client

import com.fasterxml.jackson.databind.ObjectMapper
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.model.JIRALink
import net.nemerosa.ontrack.extension.jira.model.JIRAStatus
import net.nemerosa.ontrack.extension.jira.model.JIRAVersion
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JIRAClientImplTest {

    @Test
    fun parseFromJIRA() {
        val ldt = JIRAClientImpl.parseFromJIRA("2014-06-05T14:39:51.943+0000")
        assertEquals(2014, ldt.year)
        assertEquals(6, ldt.monthValue)
        assertEquals(5, ldt.dayOfMonth)
        assertEquals(14, ldt.hour)
        assertEquals(51, ldt.second)
    }

    @Test
    fun toIssue() {
        // Configuration to test with
        val config = JIRAConfiguration("Test", "http://jira", "user", "secret")
        // Issue to parse
        val node = ObjectMapper().readTree(this::class.java.getResource("/issue.json"))
        // Parsing the issue
        val issue = JIRAClientImpl.toIssue(config, node)
        // Checking the issue
        assertNotNull(issue) {
            assertEquals("PRJ-136", issue.key)
            assertEquals("Issue summary", issue.summary)
            assertEquals(LocalDateTime.of(2014, 6, 18, 15, 12, 52, 369000000), issue.updateTime)
            assertEquals(JIRAStatus("Closed", "http://jira/images/icons/statuses/closed.png"), issue.status)
            assertEquals(listOf(JIRAVersion("1.2", false)), issue.fixVersions)
            assertEquals("dcoraboeuf", issue.assignee)
            assertEquals(listOf(JIRAVersion("1.0", true)), issue.affectedVersions)
        }
    }

    @Test
    fun `toIssue with one inward link`() {
        // Configuration to test with
        val config = JIRAConfiguration("Test", "http://jira", "user", "secret")
        // Issue to parse
        val node = ObjectMapper().readTree(this::class.java.getResource("/issue-link-inward.json"))
        // Parsing the issue
        val issue = JIRAClientImpl.toIssue(config, node)
        // Checking the issue
        assertNotNull(issue) {
            assertEquals("PRJ-136", issue.key)
            assertEquals("Issue summary", issue.summary)
            assertEquals(LocalDateTime.of(2014, 6, 18, 15, 12, 52, 369000000), issue.updateTime)
            assertEquals(JIRAStatus("Closed", "http://jira/images/icons/statuses/closed.png"), issue.status)
            assertEquals(listOf(JIRAVersion("1.2", false)), issue.fixVersions)
            assertEquals("dcoraboeuf", issue.assignee)
            assertEquals(listOf(JIRAVersion("1.0", true)), issue.affectedVersions)
            assertEquals(
                    listOf(
                            JIRALink(
                                    "PRJ-900",
                                    "http://jira/browse/PRJ-900",
                                    JIRAStatus("Open", "http://jira/images/icons/statuses/open.png"),
                                    "Blocks",
                                    "is blocked by"
                            )
                    ),
                    issue.links
            )
        }
    }

    @Test
    fun `toIssue with one outward link`() {
        // Configuration to test with
        val config = JIRAConfiguration("Test", "http://jira", "user", "secret")
        // Issue to parse
        val node = ObjectMapper().readTree(this::class.java.getResource("/issue-link-outward.json"))
        // Parsing the issue
        val issue = JIRAClientImpl.toIssue(config, node)
        // Checking the issue
        assertNotNull(issue) {
            assertEquals("PRJ-136", issue.key)
            assertEquals("Issue summary", issue.summary)
            assertEquals(LocalDateTime.of(2014, 6, 18, 15, 12, 52, 369000000), issue.updateTime)
            assertEquals(JIRAStatus("Closed", "http://jira/images/icons/statuses/closed.png"), issue.status)
            assertEquals(listOf(JIRAVersion("1.2", false)), issue.fixVersions)
            assertEquals("dcoraboeuf", issue.assignee)
            assertEquals(listOf(JIRAVersion("1.0", true)), issue.affectedVersions)
            assertEquals(
                    listOf(
                            JIRALink(
                                    "PRJ-900",
                                    "http://jira/browse/PRJ-900",
                                    JIRAStatus("Open", "http://jira/images/icons/statuses/open.png"),
                                    "Blocks",
                                    "blocks"
                            )
                    ),
                    issue.links
            )
        }
    }

    @Test
    fun `toIssue with two links`() {
        // Configuration to test with
        val config = JIRAConfiguration("Test", "http://jira", "user", "secret")
        // Issue to parse
        val node = ObjectMapper().readTree(this::class.java.getResource("/issue-link-both.json"))
        // Parsing the issue
        val issue = JIRAClientImpl.toIssue(config, node)
        // Checking the issue
        assertNotNull(issue) {
            assertEquals("PRJ-136", issue.key)
            assertEquals("Issue summary", issue.summary)
            assertEquals(LocalDateTime.of(2014, 6, 18, 15, 12, 52, 369000000), issue.updateTime)
            assertEquals(JIRAStatus("Closed", "http://jira/images/icons/statuses/closed.png"), issue.status)
            assertEquals(listOf(JIRAVersion("1.2", false)), issue.fixVersions)
            assertEquals("dcoraboeuf", issue.assignee)
            assertEquals(listOf(JIRAVersion("1.0", true)), issue.affectedVersions)
            assertEquals(
                    listOf(
                            JIRALink(
                                    "PRJ-900",
                                    "http://jira/browse/PRJ-900",
                                    JIRAStatus("Open", "http://jira/images/icons/statuses/open.png"),
                                    "Blocks",
                                    "blocks"
                            ),
                            JIRALink(
                                    "PRJ-901",
                                    "http://jira/browse/PRJ-901",
                                    JIRAStatus("Open", "http://jira/images/icons/statuses/open.png"),
                                    "Blocks",
                                    "is blocked by"
                            ),
                    ),
                    issue.links
            )
        }
    }

}
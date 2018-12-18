package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.client.ClientNotFoundException
import net.nemerosa.ontrack.client.JsonClient
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.jira.client.JIRAClient
import net.nemerosa.ontrack.extension.jira.client.JIRAClientImpl
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.model.JIRALink
import net.nemerosa.ontrack.extension.jira.model.JIRAStatus
import net.nemerosa.ontrack.extension.jira.tx.JIRASession
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import net.nemerosa.ontrack.test.assertPresent
import net.nemerosa.ontrack.tx.DefaultTransactionService
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.net.URLEncoder
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JIRAServiceExtensionTest {

    private lateinit var jiraSessionFactory: JIRASessionFactory
    private lateinit var client: JIRAClient
    private lateinit var session: JIRASession
    private lateinit var service: JIRAServiceExtension

    @Before
    fun before() {
        jiraSessionFactory = mock(JIRASessionFactory::class.java)
        val transactionService = DefaultTransactionService()
        val jiraConfigurationService = mock(JIRAConfigurationService::class.java)

        client = mock(JIRAClient::class.java)

        session = mock(JIRASession::class.java)
        `when`(session.client).thenReturn(client)

        val issueExportServiceFactory = mock(IssueExportServiceFactory::class.java)

        val propertyService = mock(PropertyService::class.java)

        service = JIRAServiceExtension(
                JIRAExtensionFeature(),
                jiraConfigurationService,
                jiraSessionFactory,
                transactionService,
                issueExportServiceFactory,
                propertyService
        )
    }

    @Test
    fun issueNotFound() {
        val config = jiraConfiguration()

        val jsonClient = mock(JsonClient::class.java)
        `when`(jsonClient.get("/rest/api/2/issue/%s?expand=names", "XXX-1")).thenThrow(
                ClientNotFoundException("XXX-1")
        )
        client = JIRAClientImpl(jsonClient)
        `when`(session.client).thenReturn(client)
        `when`(jiraSessionFactory.create(config)).thenReturn(session)

        val issue = service.getIssue(config, "XXX-1")
        assertNull(issue)
    }

    @Test
    fun getIssueTypes() {
        val issue = createIssue(1)
        val types = service.getIssueTypes(null, issue)
        assertEquals(
                setOf("Defect"),
                types
        )
    }

    @Test
    fun getIssueTypes_for_null() {
        val types = service.getIssueTypes(null, null)
        assertTrue(types.isEmpty())
    }

    @Test
    fun getMessageAnnotator() {
        val config = jiraConfiguration()
        val annotator = service.getMessageAnnotator(config)
        assertPresent(annotator) {
            val message = MessageAnnotationUtils.annotate(
                    "TEST-12, PRJ-12, PRJ-13 List of issues",
                    listOf(it)
            )
            assertEquals(
                    "<a href=\"http://jira/browse/TEST-12\">TEST-12</a>, <a href=\"http://jira/browse/PRJ-12\">PRJ-12</a>, <a href=\"http://jira/browse/PRJ-13\">PRJ-13</a> List of issues",
                    message
            )
        }
    }

    @Test
    fun extractIssueKeysFromMessage() {
        val config = jiraConfiguration()
        val issues = service.extractIssueKeysFromMessage(
                config,
                "TEST-12, PRJ-12, PRJ-13 List of issues"
        )
        assertEquals(
                setOf("TEST-12", "PRJ-12", "PRJ-13"),
                issues
        )
    }

    @Test(expected = NullPointerException::class)
    fun getLinkForAllIssues_null_config() {
        service.getLinkForAllIssues(null, Collections.emptyList())
    }

    @Test(expected = NullPointerException::class)
    fun getLinkForAllIssues_null_issues() {
        service.getLinkForAllIssues(
                jiraConfiguration(),
                null)
    }

    @Test
    fun getLinkForAllIssues_no_issue() {
        val link = service.getLinkForAllIssues(
                jiraConfiguration(),
                Collections.emptyList()
        )
        assertEquals("", link, "The link for no issue is empty" )
    }

    @Test
    fun getLinkForAllIssues_one_issue() {
        val issue = mock(Issue::class.java)
        `when`(issue.getKey()).thenReturn("PRJ-13")
        val link = service.getLinkForAllIssues(
                jiraConfiguration(),
                listOf(issue)
        )
        assertEquals("http://jira/browse/PRJ-13", link)
    }

    @Test
    fun getLinkForAllIssues_two_issues() {
        val issue1 = mock(Issue::class.java)
        `when`(issue1.key).thenReturn("PRJ-13")
        val issue2 = mock(Issue::class.java)
        `when`(issue2.key).thenReturn("PRJ-15")
        val link = service.getLinkForAllIssues(
                jiraConfiguration(),
                listOf(issue1, issue2)
        )
        assertEquals(
                "http://jira/secure/IssueNavigator.jspa?reset=true&mode=hide&jqlQuery=${
                URLEncoder.encode(
                        "key in (\"PRJ-13\",\"PRJ-15\")",
                        "UTF-8")
                }",
                link
        )
    }

    @Test
    fun `Following links`() {
        // Configuration to test with
        val config = jiraConfiguration()
        `when`(jiraSessionFactory.create(config)).thenReturn(session)
        // Creating issues
        var issue1 = createIssue(1)
        var issue2 = createIssue(2)
        var issue3 = createIssue(3)
        var issue4 = createIssue(4)
        // Linking issues together
        issue1 = issue1.withLinks(listOf(
                createLink(2, "Depends", "depends on"),
                createLink(3, "Depends", "depends on")
        ))
        issue2 = issue2.withLinks(listOf(
                createLink(1, "Depends", "is depended on by"),
                createLink(4, "Depends", "depends on")
        ))
        issue3 = issue3.withLinks(listOf(
                createLink(1, "Depends", "is depended on by")
        ))
        issue4 = issue4.withLinks(listOf(
                createLink(2, "Depends", "is depended on by")
        ))

        // Client
        `when`(client.getIssue("TEST-1", config)).thenReturn(issue1)
        `when`(client.getIssue("TEST-2", config)).thenReturn(issue2)
        `when`(client.getIssue("TEST-3", config)).thenReturn(issue3)
        `when`(client.getIssue("TEST-4", config)).thenReturn(issue4)

        // Links from 1
        var issues = mutableMapOf<String, JIRAIssue>()
        service.followLinks(config, issue1, setOf("Depends"), issues)
        assertEquals(
                setOf(
                        "TEST-1", "TEST-2", "TEST-3", "TEST-4"
                ),
                issues.values.map { it.key }.toSet()
        )
        // Links from 4
        issues = mutableMapOf()
        service.followLinks(config, issue4, setOf("Depends"), issues)
        assertEquals(
                setOf(
                        "TEST-1", "TEST-2", "TEST-3", "TEST-4"
                ),
                issues.values.map { it.key }.toSet()
        )
    }

    private fun createLink(i: Int, name: String, relation: String) =
            JIRALink(
                    "TEST-$i",
                    "...",
                    JIRAStatus("Open", "..."),
                    name,
                    relation
            )

    private fun createIssue(i: Int) =
            JIRAIssue(
                    "http://host/browser/TEST-$i",
                    "TEST-$i",
                    "Issue $i",
                    JIRAStatus("Open", "..."),
                    "",
                    Time.now(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    "Defect",
                    emptyList()
            )

    private fun jiraConfiguration() =
            JIRAConfiguration("test", "http://jira", "user", "secret")

}

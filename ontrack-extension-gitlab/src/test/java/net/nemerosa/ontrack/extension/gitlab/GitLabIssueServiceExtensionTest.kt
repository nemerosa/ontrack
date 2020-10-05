package net.nemerosa.ontrack.extension.gitlab

import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClient
import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClientFactory
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssue
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueServiceConfiguration
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueWrapper
import net.nemerosa.ontrack.extension.gitlab.service.GitLabConfigurationService
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.stale.StaleExtensionFeature
import org.gitlab4j.api.Constants
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*

class GitLabIssueServiceExtensionTest {

    private lateinit var extension: GitLabIssueServiceExtension
    private lateinit var configuration: GitLabIssueServiceConfiguration
    private lateinit var configurationService: GitLabConfigurationService
    private lateinit var engineConfiguration: GitLabConfiguration
    private lateinit var gitHubClientFactory: OntrackGitLabClientFactory
    private lateinit var issueWrapper: GitLabIssueWrapper

    @Before
    fun init() {
        configurationService = mock(GitLabConfigurationService::class.java)
        gitHubClientFactory = mock(OntrackGitLabClientFactory::class.java)
        val issueExportServiceFactory = mock(IssueExportServiceFactory::class.java)
        extension = GitLabIssueServiceExtension(
                GitLabExtensionFeature(GitExtensionFeature(SCMExtensionFeature(), StaleExtensionFeature())),
                issueExportServiceFactory,
                configurationService,
                gitHubClientFactory
        )
        engineConfiguration = GitLabConfiguration(
                "test",
                "url",
                "",
                "",
                false
        )
        configuration = GitLabIssueServiceConfiguration(
                engineConfiguration,
                "nemerosa/ontrack"
        )
        issueWrapper = GitLabIssueWrapper(
                GitLabIssue().apply {
                    id = 1
                    webUrl = "url/1"
                    title = "Issue 1"
                    state = Constants.IssueState.OPENED
                    updatedAt = Date()
                    labels = emptyList()
                },
                "url/xxx"
        )
    }

    @Test
    fun list_of_configurations_is_not_exposed() {
        assertTrue(extension.configurationList.isEmpty())
    }

    @Test
    fun get_configuration_by_name() {
        `when`(configurationService.getConfiguration("test")).thenReturn(
                engineConfiguration
        )
        val configuration = extension.getConfigurationByName("test:nemerosa/ontrack")
        assertNotNull(configuration)
        assertEquals("test:nemerosa/ontrack", configuration.name)
        assertEquals("gitlab", configuration.serviceId)
    }

    @Test(expected = IllegalStateException::class)
    fun get_configuration_by_name_using_wrong_id() {
        extension.getConfigurationByName("test")
    }

    @Test
    fun message_annotator() {
        val messageAnnotator = extension.getMessageAnnotator(configuration).orElse(null)
        assertNotNull(messageAnnotator)
        val messageAnnotations = messageAnnotator.annotate("Message for #12")
        assertEquals(2, messageAnnotations.size.toLong())
        val annotationList = ArrayList(messageAnnotations)
        run {
            val annotation = annotationList[0]
            assertNull(annotation.type)
            assertEquals("Message for ", annotation.text)
            assertTrue(annotation.attributes.isEmpty())
        }
        run {
            val annotation = annotationList[1]
            assertEquals("a", annotation.type)
            assertEquals("#12", annotation.text)
            assertEquals(
                    Collections.singletonMap("href", "url/nemerosa/ontrack/issues/12"),
                    annotation.attributes
            )
        }
    }

    @Test
    fun no_link_for_all_issues() {
        val issue = mock(Issue::class.java)
        val link = extension.getLinkForAllIssues(configuration, listOf(issue))
        assertNull(link)
    }

    @Test
    fun get_issue_from_display_key() {
        val issue = get_issue_test("#16", 16)
        assertNotNull(issue)
        assertEquals(issueWrapper, issue)
    }

    @Test
    fun get_issue_from_key() {
        val issue = get_issue_test("16", 16)
        assertNotNull(issue)
        assertEquals(issueWrapper, issue)
    }

    @Test
    fun get_issue_not_found() {
        assertNull(get_issue_test("18", 0))
    }

    protected fun get_issue_test(token: String, id: Int): Issue? {
        val client = mock(OntrackGitLabClient::class.java)
        `when`(client.getIssue(configuration.repository, id)).thenReturn(issueWrapper)
        `when`(gitHubClientFactory.create(configuration.configuration)).thenReturn(client)
        return extension.getIssue(configuration, token)
    }

    @Test
    fun issueServiceIdentifierContainsBothConfigurationAndRepository() {
        `when`(configurationService.getConfiguration("Test")).thenReturn(
                GitLabConfiguration(
                        "Test",
                        "https://gitlab.test.com", null, null,
                        false
                )
        )
        val configuration = extension.getConfigurationByName("Test:nemerosa/ontrack")
        assertEquals("gitlab", configuration.serviceId)
        assertEquals("Test:nemerosa/ontrack", configuration.name)
        assertTrue(configuration is GitLabIssueServiceConfiguration)
        val issueServiceConfiguration = configuration as GitLabIssueServiceConfiguration
        assertEquals("Test", issueServiceConfiguration.configuration.name)
        assertEquals("https://gitlab.test.com", issueServiceConfiguration.configuration.url)
        assertEquals("nemerosa/ontrack", issueServiceConfiguration.repository)
    }

    @Test
    fun getIssueId_full() {
        val o = extension.getIssueId(configuration, "#12")
        assertTrue(o.isPresent)
        assertEquals("12", o.orElse(null))
    }

    @Test
    fun getIssueId_numeric() {
        val o = extension.getIssueId(configuration, "12")
        assertTrue(o.isPresent)
        assertEquals("12", o.orElse(null))
    }

    @Test
    fun getIssueId_not_valid() {
        val o = extension.getIssueId(configuration, "mm")
        assertFalse(o.isPresent)
    }

    @Test
    fun extractIssueKeysFromMessage_none() {
        val keys = extension.extractIssueKeysFromMessage(configuration, "TEST-1 No GitHub issue")
        assertTrue(keys.isEmpty())
    }

    @Test
    fun extractIssueKeysFromMessage_one() {
        val keys = extension.extractIssueKeysFromMessage(configuration, "#12 One GitHub issue")
        assertEquals(
                setOf("12"),
                keys
        )
    }

    @Test
    fun extractIssueKeysFromMessage_two() {
        val keys = extension.extractIssueKeysFromMessage(configuration, "#12 Two GitHub #45 issue")
        assertEquals(
                setOf("12", "45"),
                keys
        )
    }

    @Test
    fun getIssueId_no_prefix() {
        assertEquals(14, extension.getIssueId("14").toLong())
    }

    @Test
    fun getIssueId_with_prefix() {
        assertEquals(14, extension.getIssueId("#14").toLong())
    }


}
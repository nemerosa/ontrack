package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.model.GitHubIssue
import net.nemerosa.ontrack.extension.github.model.GitHubState
import net.nemerosa.ontrack.extension.github.model.GitHubUser
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.extension.github.service.GitHubIssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class GitHubIssueServiceExtensionTest {

    private lateinit var extension: GitHubIssueServiceExtension
    private lateinit var configuration: IssueServiceConfiguration
    private lateinit var configurationService: GitHubConfigurationService

    @Before
    fun init() {
        configurationService = mock(GitHubConfigurationService::class.java)
        val gitHubClientFactory = mock(OntrackGitHubClientFactory::class.java)
        val issueExportServiceFactory = mock(IssueExportServiceFactory::class.java)
        extension = GitHubIssueServiceExtension(
                GitHubExtensionFeature(GitExtensionFeature(SCMExtensionFeature())),
                configurationService,
                gitHubClientFactory,
                issueExportServiceFactory
        )
        val engineConfiguration = GitHubEngineConfiguration(
                "test",
                "url",
                "",
                "",
                ""
        )
        configuration = GitHubIssueServiceConfiguration(
                engineConfiguration,
                "nemerosa/ontrack"
        )
    }

    @Test
    fun issueServiceIdentifierContainsBothConfigurationAndRepository() {
        `when`(configurationService.getConfiguration("Test")).thenReturn(
                GitHubEngineConfiguration(
                        "Test", null, null, null, null
                )
        )
        val configuration = extension.getConfigurationByName("Test:nemerosa/ontrack")
        assertEquals("github", configuration.serviceId)
        assertEquals("Test:nemerosa/ontrack", configuration.name)
        assertTrue(configuration is GitHubIssueServiceConfiguration)
        val issueServiceConfiguration = configuration as GitHubIssueServiceConfiguration
        assertEquals("Test", issueServiceConfiguration.configuration.name)
        assertEquals("https://github.com", issueServiceConfiguration.configuration.url)
        assertEquals("nemerosa/ontrack", issueServiceConfiguration.repository)
    }

    @Test
    fun getIssueId_full() {
        val o = extension.getIssueId(configuration, "#12")
        assertTrue(o.isPresent)
        assertEquals("12", o.get())
    }

    @Test
    fun getIssueId_numeric() {
        val o = extension.getIssueId(configuration, "12")
        assertTrue(o.isPresent)
        assertEquals("12", o.get())
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


    @Test
    fun `Message regular expression`() {
        val issue = GitHubIssue(
                id = 625,
                url = "...",
                summary = "...",
                body = "...",
                bodyHtml = "...",
                assignee = GitHubUser("login", "..."),
                labels = emptyList(),
                state = GitHubState.open,
                milestone = null,
                createdAt = Time.now(),
                updateTime = Time.now(),
                closedAt = null
        )
        val regex = extension.getMessageRegex(configuration, issue).toRegex()
        kotlin.test.assertTrue(regex.containsMatchIn("#625"))
        kotlin.test.assertTrue(regex.containsMatchIn("#625 at the beginning"))
        kotlin.test.assertTrue(regex.containsMatchIn("In the #625 middle"))
        kotlin.test.assertTrue(regex.containsMatchIn("In the end #625"))
        kotlin.test.assertTrue(regex.containsMatchIn("#625: with separator"))
        kotlin.test.assertFalse(regex.containsMatchIn("Too many #6250 digits"))
        kotlin.test.assertFalse(regex.containsMatchIn("Too many digits #6250"))
    }

}
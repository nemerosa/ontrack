package net.nemerosa.ontrack.extension.github

import io.mockk.every
import io.mockk.mockk
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
import net.nemerosa.ontrack.extension.stale.StaleExtensionFeature
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class GitHubIssueServiceExtensionTest {

    private lateinit var extension: GitHubIssueServiceExtension
    private lateinit var configuration: IssueServiceConfiguration
    private lateinit var configurationService: GitHubConfigurationService

    @BeforeEach
    fun init() {
        configurationService = mockk<GitHubConfigurationService>()
        val gitHubClientFactory = mockk<OntrackGitHubClientFactory>()
        val issueExportServiceFactory = mockk<IssueExportServiceFactory>()
        extension = GitHubIssueServiceExtension(
            GitHubExtensionFeature(GitExtensionFeature(SCMExtensionFeature(), StaleExtensionFeature())),
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
        every {
            configurationService.getConfiguration("Test")
        } returns GitHubEngineConfiguration(
            "Test", null, null, null, null
        )
        val configuration = extension.getConfigurationByName("Test:nemerosa/ontrack")
        assertNotNull(configuration) {
            assertEquals("github", it.serviceId)
            assertEquals("Test:nemerosa/ontrack", it.name)
            assertTrue(configuration is GitHubIssueServiceConfiguration)
            assertEquals("Test", configuration.configuration.name)
            assertEquals("https://github.com", configuration.configuration.url)
            assertEquals("nemerosa/ontrack", configuration.repository)
        }
    }

    @Test
    fun getIssueId_full() {
        val o = extension.getIssueId(configuration, "#12")
        assertEquals("12", o)
    }

    @Test
    fun getIssueId_numeric() {
        val o = extension.getIssueId(configuration, "12")
        assertEquals("12", o)
    }

    @Test
    fun getIssueId_not_valid() {
        val o = extension.getIssueId(configuration, "mm")
        assertNull(o)
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
        assertTrue(regex.containsMatchIn("#625"))
        assertTrue(regex.containsMatchIn("#625 at the beginning"))
        assertTrue(regex.containsMatchIn("In the #625 middle"))
        assertTrue(regex.containsMatchIn("In the end #625"))
        assertTrue(regex.containsMatchIn("#625: with separator"))
        assertFalse(regex.containsMatchIn("Too many #6250 digits"))
        assertFalse(regex.containsMatchIn("Too many digits #6250"))
    }

}
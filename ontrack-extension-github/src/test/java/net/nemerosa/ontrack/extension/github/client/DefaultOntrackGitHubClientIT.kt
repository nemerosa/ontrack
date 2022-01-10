package net.nemerosa.ontrack.extension.github.client

import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.TestOnGitHub
import net.nemerosa.ontrack.extension.github.app.DefaultGitHubAppTokenService
import net.nemerosa.ontrack.extension.github.app.MockGitHubAppClient
import net.nemerosa.ontrack.extension.github.githubTestConfigReal
import net.nemerosa.ontrack.extension.github.githubTestEnv
import net.nemerosa.ontrack.extension.github.model.GitHubRepositoryPermission
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestOnGitHub
class DefaultOntrackGitHubClientIT {

    private lateinit var client: OntrackGitHubClient

    @BeforeEach
    fun init() {
        client = DefaultOntrackGitHubClient(
            configuration = githubTestConfigReal(),
            gitHubAppTokenService = DefaultGitHubAppTokenService(
                gitHubAppClient = MockGitHubAppClient(),
                ontrackConfigProperties = OntrackConfigProperties(),
            ),
            applicationLogService = mockk(relaxed = true),
            timeout = Duration.ofSeconds(60),
        )
    }

    @Test
    fun `Getting the list of organizations`() {
        val organizations = client.organizations
        assertTrue(
            organizations.any { it.login == githubTestEnv.organization },
            "Can get the list of organizations"
        )
    }

    @Test
    fun `Getting the list of repositories for an organization`() {
        val repos = client.findRepositoriesByOrganization(githubTestEnv.organization)
        assertTrue(
            repos.any { it.name == githubTestEnv.repository },
            "Found the ${githubTestEnv.repository} repository in the ${githubTestEnv.organization} organization"
        )
    }

    @Test
    fun `Getting an issue`() {
        val issue = client.getIssue(githubTestEnv.fullRepository, githubTestEnv.issue)
        assertNotNull(issue, "Issue ${githubTestEnv.fullRepository}#${githubTestEnv.issue} has been found") {
            assertEquals("#${githubTestEnv.issue}", it.displayKey)
        }
    }

    @Test
    fun `Getting an unknown issue`() {
        val id = Int.MAX_VALUE / 2
        val issue = client.getIssue(githubTestEnv.fullRepository, id)
        assertNull(issue, "Issue ${githubTestEnv.fullRepository}#$id cannot be found found")
    }

    @Test
    fun `Getting a PR`() {
        val pr = client.getPullRequest(githubTestEnv.fullRepository, githubTestEnv.pr)
        assertNotNull(pr, "PR ${githubTestEnv.fullRepository}#${githubTestEnv.pr} has been found") {
            assertEquals("#${githubTestEnv.pr}", it.key)
        }
    }

    @Test
    fun `Getting an unknown PR`() {
        val id = Int.MAX_VALUE / 2
        val pr = client.getPullRequest(githubTestEnv.fullRepository, id)
        assertNull(pr, "Issue ${githubTestEnv.fullRepository}#$id cannot be found found")
    }

    @Test
    fun `Getting the teams for an organization`() {
        val teams = client.getOrganizationTeams(githubTestEnv.organization)
        assertNotNull(teams, "Getting the teams") { list ->
            assertTrue(
                list.any { it.slug == githubTestEnv.team },
                "Found the ${githubTestEnv.team} team in the ${githubTestEnv.organization} organization"
            )
        }
    }

    @Test
    fun `Getting the repositories for a team`() {
        val teamRepos = client.getTeamRepositories(githubTestEnv.organization, githubTestEnv.team)
        assertNotNull(teamRepos, "Getting the repositories for a team") { list ->
            val teamRepo = list.find {
                it.repository == githubTestEnv.repository
            }
            assertNotNull(teamRepo, "Found ${githubTestEnv.repository} in ${githubTestEnv.team} team") {
                assertEquals(GitHubRepositoryPermission.READ, it.permission)
            }
        }
    }

}

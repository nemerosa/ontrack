package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.extension.github.githubTestConfigReal
import net.nemerosa.ontrack.extension.github.githubTestEnv
import net.nemerosa.ontrack.extension.github.model.GitHubRepositoryPermission
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultOntrackGitHubClientIT {

    private lateinit var client: OntrackGitHubClient

    @Before
    fun init() {
        client = DefaultOntrackGitHubClient(
            configuration = githubTestConfigReal()
        )
    }

    @Test
    fun `Getting the list of organizations`() {
        val orgs = client.organizations
        assertTrue(
            orgs.any { it.login == githubTestEnv.organization },
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
            assertEquals("Collect repositories from teams, not teams from repositories", it.summary)
        }
    }

    @Test
    fun `Getting an unknown issue`() {
        val id = Int.MAX_VALUE / 2
        val issue = client.getIssue(githubTestEnv.fullRepository, id)
        assertNull(issue, "Issue ${githubTestEnv.fullRepository}#$id cannot be found found")
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

package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.model.*
import org.apache.commons.lang3.StringUtils
import org.eclipse.egit.github.core.Issue
import org.eclipse.egit.github.core.Label
import org.eclipse.egit.github.core.Milestone
import org.eclipse.egit.github.core.User
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.client.RequestException
import org.eclipse.egit.github.core.service.IssueService
import org.eclipse.egit.github.core.service.OrganizationService
import org.eclipse.egit.github.core.service.RepositoryService
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.time.LocalDateTime
import java.util.*

class DefaultOntrackGitHubClient(
        private val configuration: GitHubEngineConfiguration
) : OntrackGitHubClient {

    private val logger = LoggerFactory.getLogger(OntrackGitHubClient::class.java)

    override val repositories: List<String>
        get() {
            logger.debug("[github] Getting repository list")
            // Getting a client
            val client = createGitHubClient()
            // Service
            val repositoryService = RepositoryService(client)
            // Gets the repository names
            return try {
                repositoryService.repositories.map { it.name }
            } catch (e: IOException) {
                throw OntrackGitHubClientException(e)
            }
        }

    override val organizations: List<GitHubUser>
        get() {
            // Getting a client
            val client = createGitHubClient()
            // Service
            val service = OrganizationService(client)
            // Gets the organization names
            return try {
                service.organizations.map {
                    GitHubUser(
                            login = it.login,
                            url = it.htmlUrl
                    )
                }
            } catch (e: IOException) {
                throw OntrackGitHubClientException(e)
            }
        }

    override fun findRepositoriesByOrganization(organization: String): List<String> {
        // Getting a client
        val client = createGitHubClient()
        // Service
        val repositoryService = RepositoryService(client)
        // Gets the repository names
        return try {
            repositoryService.getRepositories(organization).map { it.name }
        } catch (e: IOException) {
            throw OntrackGitHubClientException(e)
        }
    }

    override fun getIssue(repository: String, id: Int): GitHubIssue? {
        // Logging
        logger.debug("[github] Getting issue {}/{}", repository, id)
        // Getting a client
        val client = createGitHubClient()
        // Issue service using this client
        val service = IssueService(client)
        // Gets the repository for this project
        val owner = repository.substringBefore("/")
        val name = repository.substringAfter("/")
        val issue: Issue = try {
            service.getIssue(owner, name, id)
        } catch (ex: RequestException) {
            return if (ex.status == 404) {
                null
            } else {
                throw OntrackGitHubClientException(ex)
            }
        } catch (e: IOException) {
            throw OntrackGitHubClientException(e)
        }
        // Conversion
        return GitHubIssue(
                id,
                issue.htmlUrl,
                issue.title,
                issue.bodyText,
                issue.bodyHtml,
                toUser(issue.assignee),
                toLabels(issue.labels),
                toState(issue.state),
                toMilestone(repository, issue.milestone),
                toDateTime(issue.createdAt),
                toDateTime(issue.updatedAt),
                toDateTime(issue.closedAt)
        )
    }

    private fun createGitHubClient(): GitHubClient {
        // GitHub client (non authentified)
        val client: GitHubClient = object : GitHubClient() {
            override fun configureRequest(request: HttpURLConnection): HttpURLConnection {
                val connection = super.configureRequest(request)
                connection.setRequestProperty(HEADER_ACCEPT, "application/vnd.github.v3.full+json")
                return connection
            }
        }
        // Authentication
        val oAuth2Token = configuration.oauth2Token
        if (StringUtils.isNotBlank(oAuth2Token)) {
            client.setOAuth2Token(oAuth2Token)
        } else {
            val user = configuration.user
            val password = configuration.password
            if (StringUtils.isNotBlank(user)) {
                client.setCredentials(user, password)
            }
        }
        return client
    }

    private fun toDateTime(date: Date): LocalDateTime = Time.from(date, null)

    private fun toMilestone(repository: String, milestone: Milestone): GitHubMilestone =
            milestone.run {
                GitHubMilestone(
                        title,
                        toState(state),
                        number, String.format(
                        "%s/%s/issues?milestone=%d&state=open",
                        configuration.url,
                        repository,
                        number
                ))
            }

    private fun toState(state: String): GitHubState = GitHubState.valueOf(state)

    private fun toLabels(labels: List<Label>): List<GitHubLabel> = labels
            .map { label: Label ->
                GitHubLabel(
                        label.name,
                        label.color
                )
            }

    private fun toUser(user: User): GitHubUser =
            user.run {
                GitHubUser(
                        login,
                        htmlUrl
                )
            }

}
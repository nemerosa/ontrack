package net.nemerosa.ontrack.extension.github.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.github.model.*
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import org.apache.commons.lang3.StringUtils
import org.eclipse.egit.github.core.*
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.client.RequestException
import org.eclipse.egit.github.core.service.IssueService
import org.eclipse.egit.github.core.service.OrganizationService
import org.eclipse.egit.github.core.service.PullRequestService
import org.eclipse.egit.github.core.service.RepositoryService
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.MediaType
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
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
            repositoryService.getOrgRepositories(organization).map { it.name }
        } catch (e: IOException) {
            throw OntrackGitHubClientException(e)
        }
    }

    override fun getRepositoryLastModified(repo: String): LocalDateTime? {
        // Logging
        logger.debug("[github] Getting repository last modification {}", repo)
        // Getting a client
        val client = createGitHubClient()
        // Service
        val repositoryService = RepositoryService(client)
        // Gets the repository
        val owner = repo.substringBefore("/")
        val name = repo.substringAfter("/")
        val repository = repositoryService.getRepository(owner, name)
        // Last modification date
        return repository.pushedAt?.let { Time.from(it, null) }
    }

    override fun getRepositoryTeams(repo: String): List<GitHubTeam>? {
        // Getting a client
        val client = createGitHubRestTemplate()
        // Calling
        return client("Getting teams for $repo") {
            getForObject("/repos/$repo/teams", JsonNode::class.java)?.map {
                it.parse<GitHubTeam>()
            }
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
            toDateTime(issue.createdAt)!!,
            toDateTime(issue.updatedAt) ?: toDateTime(issue.createdAt)!!,
            toDateTime(issue.closedAt)
        )
    }

    private operator fun <T> RestTemplate.invoke(
        message: String,
        code: RestTemplate.() -> T
    ): T {
        logger.debug("[github] {}", message)
        return try {
            code()
        } catch (ex: RestClientResponseException) {
            @Suppress("UNNECESSARY_SAFE_CALL")
            val contentType: Any? = ex.responseHeaders?.contentType
            if (contentType != null && contentType is MediaType && contentType.includes(MediaType.APPLICATION_JSON)) {
                val json = ex.responseBodyAsString
                try {
                    val error: GitHubErrorMessage = json.parseAsJson().parse()
                    throw GitHubErrorsException(message, error)
                } catch (_: JsonParseException) {
                    throw ex
                }
            } else {
                throw ex
            }
        }
    }

    override fun createGitHubRestTemplate(): RestTemplate = RestTemplateBuilder()
        .rootUri(getApiRoot(configuration.url))
        .basicAuthentication(
            configuration.user,
            if (configuration.oauth2Token.isNullOrBlank()) {
                configuration.password
            } else {
                configuration.oauth2Token
            }
        )
        .build()

    override fun createGitHubClient(): GitHubClient {
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

    override fun getPullRequest(repository: String, id: Int): GitPullRequest? {
        // Getting a client
        val client = createGitHubClient()
        // PR service using this client
        val service = PullRequestService(client)
        // Getting the PR
        val pr: PullRequest
        pr = try {
            service.getPullRequest(RepositoryId.createFromId(repository), id)
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
        return GitPullRequest(
                id = id,
                key = "#$id",
                source = pr.head.ref,
                target = pr.base.ref,
                title = pr.title,
                status = pr.state,
                url = pr.htmlUrl
        )
    }

    private fun toDateTime(date: Date?): LocalDateTime? = Time.from(date, null)

    private fun toMilestone(repository: String, milestone: Milestone?): GitHubMilestone? =
        milestone?.run {
            GitHubMilestone(
                title,
                toState(state),
                number, String.format(
                    "%s/%s/issues?milestone=%d&state=open",
                    configuration.url,
                    repository,
                    number
                )
            )
        }

    private fun toState(state: String): GitHubState = GitHubState.valueOf(state)

    private fun toLabels(labels: List<Label>?): List<GitHubLabel> = labels
        ?.map { label: Label ->
            GitHubLabel(
                label.name,
                label.color
            )
        } ?: emptyList()

    private fun toUser(user: User?): GitHubUser? =
        user?.run {
            GitHubUser(
                login,
                htmlUrl
            )
        }

    companion object {
        fun getApiRoot(url: String): String =
            if (url.trimEnd('/') == "https://github.com") {
                "https://api.github.com"
            } else {
                "${url.trimEnd('/')}/api/v3"
            }
    }

    private class GitHubErrorsException(
        message: String,
        error: GitHubErrorMessage
    ) : BaseException(error.format(message))

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GitHubErrorMessage(
        val message: String,
        val errors: List<GitHubError>?
    ) {
        fun format(message: String): String = mapOf(
            "message" to message,
            "exception" to this
        ).toString()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GitHubError(
        val resource: String,
        val field: String,
        val code: String
    )

}
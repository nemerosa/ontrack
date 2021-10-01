package net.nemerosa.ontrack.extension.github.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.github.model.*
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.getBooleanField
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import org.apache.commons.lang3.StringUtils
import org.eclipse.egit.github.core.*
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.client.RequestException
import org.eclipse.egit.github.core.service.IssueService
import org.eclipse.egit.github.core.service.OrganizationService
import org.eclipse.egit.github.core.service.PullRequestService
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.MediaType
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.net.HttpURLConnection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class DefaultOntrackGitHubClient(
    private val configuration: GitHubEngineConfiguration
) : OntrackGitHubClient {

    private val logger = LoggerFactory.getLogger(OntrackGitHubClient::class.java)

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

    override fun findRepositoriesByOrganization(organization: String): List<GitHubRepository> =
        paginateGraphQL(
            message = "Getting repositories for organization $organization",
            query = """
                query OrgRepositories(${'$'}login: String!, ${'$'}after: String) {
                  organization(login: ${'$'}login) {
                    repositories(first: 100, after: ${'$'}after) {
                      pageInfo {
                        hasNextPage
                        endCursor
                      }
                      nodes {
                        name
                        description
                        updatedAt
                        createdAt
                      }
                    }
                  }
                }
            """,
            variables = mapOf("login" to organization),
            collectionAt = listOf("organization", "repositories"),
            nodes = true
        ) { node ->
            GitHubRepository(
                name = node.path("name").asText(),
                description = node.path("description").asText(),
                lastUpdate = node.path("updatedAt")?.asText()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) },
                createdAt = node.path("createdAt")?.asText()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
            )
        } ?: throw GitHubNoGraphQLResponseException("Getting repositories for organization $organization")

    private fun getRepositoryParts(repository: String): Pair<String, String> {
        val login = repository.substringBefore("/")
        val name = repository.substringAfter("/")
        return login to name
    }

    override fun getIssue(repository: String, id: Int): GitHubIssue? {
        // Logging
        logger.debug("[github] Getting issue {}/{}", repository, id)
        // Getting a client
        val client = createGitHubClient()
        // Issue service using this client
        val service = IssueService(client)
        // Gets the repository for this project
        val (owner, name) = getRepositoryParts(repository)
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
            id = id,
            url = issue.htmlUrl,
            summary = issue.title,
            body = issue.bodyText ?: "",
            bodyHtml = issue.bodyHtml ?: "",
            assignee = toUser(issue.assignee),
            labels = toLabels(issue.labels),
            state = toState(issue.state),
            milestone = toMilestone(repository, issue.milestone),
            createdAt = toDateTime(issue.createdAt)!!,
            updateTime = toDateTime(issue.updatedAt) ?: toDateTime(issue.createdAt)!!,
            closedAt = toDateTime(issue.closedAt)
        )
    }

    override fun getOrganizationTeams(login: String): List<GitHubTeam>? =
        paginateGraphQL(
            message = "Getting teams for $login organization",
            query = """
               query OrgTeams(${'$'}login: String!, ${'$'}after: String) {
                  organization(login: ${'$'}login) {
                    teams(first: 100, after: ${'$'}after) {
                      pageInfo {
                        hasNextPage
                        endCursor
                      }
                      nodes {
                        slug
                        name
                        description
                        url
                      }
                    }
                  }
                } 
            """,
            variables = mapOf(
                "login" to login
            ),
            collectionAt = listOf("organization", "teams"),
            nodes = true
        ) { teamNode ->
            GitHubTeam(
                slug = teamNode.path("slug").asText(),
                name = teamNode.path("name").asText(),
                description = teamNode.path("description").asText(),
                html_url = teamNode.path("url").asText()
            )
        }

    override fun getTeamRepositories(login: String, teamSlug: String): List<GitHubTeamRepository>? =
        paginateGraphQL(
            message = "Getting repositories for team $teamSlug in organization $login",
            query = """
                query TeamRepositories(${'$'}login: String!, ${'$'}team: String!, ${'$'}after: String) {
                  organization(login: ${'$'}login) {
                    team(slug: ${'$'}team) {
                      repositories(first: 100, after: ${'$'}after) {
                        pageInfo {
                          hasNextPage
                          endCursor
                        }
                        edges {
                          permission
                          node {
                            name
                          }
                        }
                      }
                    }
                  }
                }
            """,
            variables = mapOf(
                "login" to login,
                "team" to teamSlug
            ),
            collectionAt = listOf("organization", "team", "repositories"),
            nodes = false
        ) { edge ->
            GitHubTeamRepository(
                repository = edge.path("node").path("name").asText(),
                permission = edge.path("permission").asText().let { GitHubRepositoryPermission.valueOf(it) }
            )
        }

    private fun <T> paginateGraphQL(
        message: String,
        query: String,
        variables: Map<String, *> = emptyMap<String, Any>(),
        collectionAt: List<String>,
        nodes: Boolean = false,
        code: (node: JsonNode) -> T
    ): List<T>? {
        val results = mutableListOf<T>()
        var hasNext = true
        var after: String? = null
        while (hasNext) {
            val actualVariables = variables + ("after" to after)
            try {
                graphQL(message, query, actualVariables) { data ->
                    var page = data
                    collectionAt.forEach { childName ->
                        page = page.path(childName)
                    }
                    // List of results
                    val list = if (nodes) {
                        page.path("nodes")
                    } else {
                        page.path("edges")
                    }
                    // Conversion
                    list.forEach { item ->
                        results += code(item)
                    }
                    // Pagination
                    val pageInfo = page.path("pageInfo")
                    hasNext = pageInfo.path("hasNextPage").asBoolean()
                    if (hasNext) {
                        after = pageInfo.path("endCursor").asText()
                    }
                }
            } catch (_: GitHubNoGraphQLResponseException) {
                return null
            }
        }
        return results
    }

    private fun <T> graphQL(
        message: String,
        query: String,
        variables: Map<String, *> = emptyMap<String, Any>(),
        code: (data: JsonNode) -> T
    ): T {
        // Getting a client
        val client = createGitHubRestTemplate()
        // GraphQL call
        return client(message) {
            val response = postForObject(
                "/graphql",
                mapOf(
                    "query" to query,
                    "variables" to variables
                ),
                JsonNode::class.java
            )
            if (response != null) {
                val errors = response.path("errors")
                if (errors != null && errors.isArray && errors.size() > 0) {
                    val messages: List<String> = errors.map { it.path("message").asText() }
                    throw GitHubNoGraphQLErrorsException(message, messages)
                } else {
                    val data = response.path("data")
                    code(data)
                }
            } else {
                throw GitHubNoGraphQLResponseException(message)
            }
        }
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
            // TODO #881
//        .apply {
//            configuration.authenticate(this)
//        }
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

    override fun getPullRequest(repository: String, id: Int, ignoreError: Boolean): GitPullRequest? {
        // Getting a client
        val client = createGitHubClient()
        // PR service using this client
        val service = PullRequestService(client)
        // Getting the PR
        val pr: PullRequest = try {
            service.getPullRequest(RepositoryId.createFromId(repository), id)
        } catch (ex: RequestException) {
            return if (ex.status == 404 || ignoreError) {
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

    override fun getRepositorySettings(repository: String, askVisibility: Boolean): GitHubRepositorySettings {
        val (login, name) = getRepositoryParts(repository)
        return graphQL(
            message = "Get description for $repository",
            query = """
                query Description(${'$'}login: String!, ${'$'}name: String!) {
                  organization(login: ${'$'}login) {
                    repository(name: ${'$'}name) {
                        description
                        defaultBranchRef {
                            name
                        }
                        hasWikiEnabled
                        hasIssuesEnabled
                        hasProjectsEnabled
                    }
                  }
                }
            """,
            variables = mapOf("login" to login, "name" to name)
        ) { data ->
            val repo = data.path("organization").path("repository")
            val description = repo.path("description")
                .asText()
                ?.takeIf { it.isNotBlank() }
            val branch = repo.path("defaultBranchRef")
                .path("name")
                .asText()
                ?.takeIf { it.isNotBlank() }
            val hasWikiEnabled = repo.getBooleanField("hasWikiEnabled") ?: false
            val hasIssuesEnabled = repo.getBooleanField("hasIssuesEnabled") ?: false
            val hasProjectsEnabled = repo.getBooleanField("hasProjectsEnabled") ?: false
            // Visibility
            var visibility: GitHubRepositoryVisibility? = null
            if (askVisibility) {
                // Unfortunately, as of now, the visibility flag is not available through the GraphQL API
                // and a REST call is therefore needed to get this information
                val rest = createGitHubRestTemplate()
                visibility = rest.getForObject("/repos/${login}/${name}", GitHubRepositoryWithVisibility::class.java)
                    ?.visibility
                    ?.uppercase()
                    ?.let { GitHubRepositoryVisibility.valueOf(it) }
            }
            // OK
            GitHubRepositorySettings(
                description = description,
                defaultBranch = branch,
                hasWikiEnabled = hasWikiEnabled,
                hasIssuesEnabled = hasIssuesEnabled,
                hasProjectsEnabled = hasProjectsEnabled,
                visibility = visibility,
            )
        }
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

    private class GitHubNoGraphQLResponseException(message: String) : BaseException(
        """
            $message
            
            No GraphQL response was returned.
        """.trimIndent()
    )

    private class GitHubNoGraphQLErrorsException(message: String, messages: List<String>) : BaseException(
        format(message, messages)
    ) {
        companion object {
            fun format(message: String, messages: List<String>): String {
                val list = messages.joinToString("\n") { " - $it" }
                return "$message\n\n$list"
            }
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
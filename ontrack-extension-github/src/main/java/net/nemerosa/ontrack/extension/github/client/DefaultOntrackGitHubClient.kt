package net.nemerosa.ontrack.extension.github.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.github.app.GitHubAppTokenService
import net.nemerosa.ontrack.extension.github.model.*
import net.nemerosa.ontrack.git.support.GitConnectionRetry
import net.nemerosa.ontrack.json.*
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * GitHub client which uses the GitHub Rest API at https://docs.github.com/en/rest
 */
class DefaultOntrackGitHubClient(
    private val configuration: GitHubEngineConfiguration,
    private val gitHubAppTokenService: GitHubAppTokenService,
    private val applicationLogService: ApplicationLogService,
    private val timeout: Duration = Duration.ofSeconds(60),
    private val retries: UInt = 3u,
    private val interval: Duration = Duration.ofSeconds(30),
) : OntrackGitHubClient {

    private val logger: Logger = LoggerFactory.getLogger(OntrackGitHubClient::class.java)

    override fun getRateLimit(): GitHubRateLimit? =
        try {
            createGitHubRestTemplate()("Gets rate limit") {
                getForObject<JsonNode>("/rate_limit").getJsonField("resources")?.run {
                    parse()
                }
            }
        } catch (_: HttpClientErrorException.NotFound) {
            // Rate limit not supported
            null
        } catch (any: Exception) {
            applicationLogService.log(
                ApplicationLogEntry.error(
                    any,
                    NameDescription.nd("github-ratelimit", "Cannot get the GitHub rate limit"),
                    "Cannot get the rate limit for GitHub at ${configuration.url}"
                ).withDetail("configuration", configuration.name)
            )
            // No rate limit
            null
        }

    override val organizations: List<GitHubUser>
        get() = if (configuration.authenticationType() == GitHubAuthenticationType.APP) {
            listOfNotNull(
                gitHubAppTokenService.getAppInstallationAccount(configuration)?.run {
                    GitHubUser(
                        login = login,
                        url = url,
                    )
                }
            )
        } else {
            // Getting a client
            val client = createGitHubRestTemplate()
            // Gets the organization names
            client("Gets list of organizations") {
                getForObject<JsonNode>("/user/orgs?per_page=100").map { node ->
                    node.parse()
                }
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
                    ?.let { parseLocalDateTime(it) },
                createdAt = node.path("createdAt")?.asText()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { parseLocalDateTime(it) }
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
        val client = createGitHubRestTemplate()
        // Gets the repository for this project
        val (owner, name) = getRepositoryParts(repository)
        // Gets the issue
        return try {
            client("Get issue $repository#$id") {
                getForObject<JsonNode>("/repos/$owner/$name/issues/$id").let {
                    GitHubIssue(
                        id = id,
                        url = it.getRequiredTextField("html_url"),
                        summary = it.getRequiredTextField("title"),
                        body = it.getTextField("body") ?: "",
                        bodyHtml = it.getTextField("body") ?: "",
                        assignee = it.getUserField("assignee"),
                        labels = it.getLabels(),
                        state = it.getState(),
                        milestone = it.getMilestone(),
                        createdAt = it.getCreatedAt(),
                        updateTime = it.getUpdatedAt(),
                        closedAt = it.getClosedAt(),
                    )
                }
            }
        } catch (ex: GitHubErrorsException) {
            if (ex.status == 404) {
                null
            } else {
                throw ex
            }
        }
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

    override fun <T> graphQL(
        message: String,
        query: String,
        variables: Map<String, *>,
        code: (data: JsonNode) -> T
    ): T {
        // Getting a client
        val client = createGitHubTemplate(graphql = true)
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
            GitConnectionRetry.retry(message, retries, interval) {
                code()
            }
        } catch (ex: RestClientResponseException) {
            @Suppress("UNNECESSARY_SAFE_CALL")
            val contentType: Any? = ex.responseHeaders?.contentType
            if (contentType != null && contentType is MediaType && contentType.includes(MediaType.APPLICATION_JSON)) {
                val json = ex.responseBodyAsString
                try {
                    val error: GitHubErrorMessage = json.parseAsJson().parse()
                    throw GitHubErrorsException(message, error, ex.rawStatusCode)
                } catch (_: JsonParseException) {
                    throw ex
                }
            } else {
                throw ex
            }
        }
    }

    override fun createGitHubRestTemplate(): RestTemplate =
        createGitHubTemplate(graphql = false)

    private fun createGitHubTemplate(graphql: Boolean): RestTemplate = RestTemplateBuilder()
        .rootUri(getApiRoot(configuration.url, graphql))
        .setConnectTimeout(timeout)
        .setReadTimeout(timeout)
        .run {
            when (configuration.authenticationType()) {
                GitHubAuthenticationType.ANONYMOUS -> this // Nothing to be done
                GitHubAuthenticationType.PASSWORD -> {
                    basicAuthentication(configuration.user, configuration.password)
                }
                GitHubAuthenticationType.USER_TOKEN -> {
                    basicAuthentication(configuration.user, configuration.oauth2Token)
                }
                GitHubAuthenticationType.TOKEN -> {
                    defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${configuration.oauth2Token}")
                }
                GitHubAuthenticationType.APP -> {
                    defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer ${gitHubAppTokenService.getAppInstallationToken(configuration)}"
                    )
                }
            }
        }
        .build()

    override fun getPullRequest(repository: String, id: Int, ignoreError: Boolean): GitPullRequest? {
        // Getting a client
        val client = createGitHubRestTemplate()
        // Gets the repository for this project
        val (owner, name) = getRepositoryParts(repository)
        // Getting the PR
        return try {
            client("Get PR $repository#$id") {
                getForObject<JsonNode?>("/repos/$owner/$name/pulls/$id")?.run {
                    GitPullRequest(
                        id = id,
                        key = "#$id",
                        source = path("head").path("ref").asText(),
                        target = path("base").path("ref").asText(),
                        title = getRequiredTextField("title"),
                        status = getRequiredTextField("state"),
                        url = getRequiredTextField("html_url"),
                    )
                }
            }
        } catch (ex: GitHubErrorsException) {
            if (ex.status == 404 || ignoreError) {
                null
            } else {
                throw ex
            }
        }
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

    private fun JsonNode.getUserField(field: String): GitHubUser? =
        getJsonField(field)?.run {
            GitHubUser(
                login = getRequiredTextField("login"),
                url = getTextField("html_url"),
            )
        }

    private fun JsonNode.getLabels(): List<GitHubLabel> {
        val field = "labels"
        return if (has(field)) {
            val list = get(field)
            list.map { node ->
                GitHubLabel(
                    name = node.getRequiredTextField("name"),
                    color = node.getRequiredTextField("color"),
                )
            }
        } else {
            emptyList()
        }
    }

    private fun JsonNode.getState(): GitHubState {
        val value = getRequiredTextField("state")
        return GitHubState.valueOf(value)
    }

    private fun JsonNode.getMilestone(): GitHubMilestone? = getJsonField("milestone")?.let { node ->
        GitHubMilestone(
            title = node.getRequiredTextField("title"),
            state = node.getState(),
            number = node.getRequiredIntField("number"),
            url = node.getRequiredTextField("html_url"),
        )
    }

    private fun JsonNode.getCreatedAt(): LocalDateTime =
        getRequiredDateTime("created_at")

    private fun JsonNode.getUpdatedAt(): LocalDateTime =
        getRequiredDateTime("updated_at")

    private fun JsonNode.getClosedAt(): LocalDateTime? =
        getDateTime("closed_at")

    private fun JsonNode.getRequiredDateTime(field: String): LocalDateTime =
        getDateTime(field) ?: throw JsonParseException("Missing field $field")

    private fun JsonNode.getDateTime(field: String): LocalDateTime? =
        getTextField(field)?.let {
            parseLocalDateTime(it)
        }

    private fun parseLocalDateTime(value: String) = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME)

    companion object {
        /**
         * Cloud root API
         */
        private const val CLOUD_ROOT_API = "https://api.github.com"

        /**
         * Gets the API URL for the given server URL, for REST or for GraphQL.
         *
         * If [graphql] is true, the URL is returned _without_ the `/graphql` suffix.
         */
        fun getApiRoot(url: String, graphql: Boolean): String =
            // Cloud version
            if (url.trimEnd('/') == "https://github.com") {
                CLOUD_ROOT_API
            }
            // Enterprise version
            else {
                val rootUrl = url.trimEnd('/')
                if (graphql) {
                    "$rootUrl/api"
                } else {
                    "$rootUrl/api/v3"
                }
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
        error: GitHubErrorMessage,
        val status: Int,
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
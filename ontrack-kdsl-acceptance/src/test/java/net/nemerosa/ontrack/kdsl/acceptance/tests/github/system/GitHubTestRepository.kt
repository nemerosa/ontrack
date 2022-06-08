package net.nemerosa.ontrack.kdsl.acceptance.tests.github.system

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.gitHubPlaygroundClient
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.gitHubPlaygroundEnv
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.Ontrack
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.extension.git.GitBranchConfigurationProperty
import net.nemerosa.ontrack.kdsl.spec.extension.git.gitBranchConfigurationProperty
import net.nemerosa.ontrack.kdsl.spec.extension.github.GitHubConfiguration
import net.nemerosa.ontrack.kdsl.spec.extension.github.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.kdsl.spec.extension.github.gitHub
import net.nemerosa.ontrack.kdsl.spec.extension.github.gitHubConfigurationProperty
import org.apache.commons.codec.binary.Base64
import java.util.*

fun withTestGitHubRepository(
    autoMerge: Boolean = false,
    code: GitHubRepositoryContext.() -> Unit,
) {
    // Unique name for the repository
    val uuid = UUID.randomUUID().toString()
    val repo = "ontrack-auto-versioning-test-$uuid"

    // Creates the repository
    gitHubPlaygroundClient.postForObject(
        "/orgs/${gitHubPlaygroundEnv.organization}/repos",
        mapOf(
            "name" to repo,
            "description" to "Test repository for auto versioning - can be deleted at any time",
            "private" to false,
            "has_issues" to false,
            "has_projects" to false,
            "has_wiki" to false
        ),
        Unit::class.java
    )

    try {

        // Creating a dummy file
        // createFile(
        //     branch = "master", path = "README.md", content = listOf(
        //         "Test repository - can be deleted"
        //     )
        // )

        // Context
        val context = GitHubRepositoryContext(repo)

        // Running the code
        context.code()


    } finally {
        // Deleting the repository
        gitHubPlaygroundClient.delete("/repos/${gitHubPlaygroundEnv.organization}/$repo")
    }
}

class GitHubRepositoryContext(
    val repository: String,
) {

    fun repositoryFile(
        path: String,
        branch: String = "main",
        content: () -> String,
    ) {
        // TODO createBranchIfNotExisting
        gitHubPlaygroundClient.put(
            "/repos/${gitHubPlaygroundEnv.organization}/${repository}/contents/$path",
            mapOf(
                "message" to "Creating $path",
                "content" to Base64.encodeBase64String(content().toByteArray()),
                "branch" to branch
            )
        )
    }

    fun createGitHubConfiguration(ontrack: Ontrack): String {
        val name = uid("gh")
        ontrack.gitHub.createConfig(
            GitHubConfiguration(
                name = name,
                url = "https://github.com",
                oauth2Token = gitHubPlaygroundEnv.token,
                autoMergeToken = gitHubPlaygroundEnv.autoMergeToken,
            )
        )
        return name
    }

    fun Project.configuredForGitHub(ontrack: Ontrack) {
        val config = createGitHubConfiguration(ontrack)
        gitHubConfigurationProperty = GitHubProjectConfigurationProperty(
            configuration = config,
            repository = "${gitHubPlaygroundEnv.organization}/$repository",
        )
    }

    fun Branch.configuredForGitHubRepository(
        ontrack: Ontrack,
        scmBranch: String = "main",
    ) {
        project.configuredForGitHub(ontrack)
        gitBranchConfigurationProperty = GitBranchConfigurationProperty(branch = scmBranch)
    }

    fun assertThatGitHubRepository(
        code: AssertionContext.() -> Unit,
    ) {
        val context = AssertionContext()
        context.code()
    }

    private fun getPR(from: String, to: String): GitHubPR? {
        var url = "/repos/${gitHubPlaygroundEnv.organization}/$repository/pulls?state=all"
        url += "&head=$from"
        url += "&base=$to"
        return gitHubPlaygroundClient.getForObject(
            url,
            JsonNode::class.java
        )?.firstOrNull()?.parse()
    }

    private fun getFile(path: String, branch: String): List<String> {
        val response = gitHubPlaygroundClient.getForObject(
            "/repos/${gitHubPlaygroundEnv.organization}/$repository/contents/$path?ref=$branch",
            GitHubContentsResponse::class.java
        )
        return response?.content?.run { String(Base64.decodeBase64(this)) }?.lines()?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    inner class AssertionContext {
        fun hasPR(from: String, to: String, checkPR: (GitHubPR?) -> Boolean = { it != null }) {
            waitUntil {
                val pr = getPR(from, to)
                checkPR(pr)
            }
        }

        fun fileContains(
            path: String,
            branch: String = "main",
            content: () -> String,
        ) {
            val expectedContent = content()
            waitUntil {
                val actualContent = getFile(path, branch).joinToString("\n")
                expectedContent in actualContent
            }
        }

    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class GitHubPR(
    val number: Int,
    val state: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
private class GitHubContentsResponse(
    val content: String,
)
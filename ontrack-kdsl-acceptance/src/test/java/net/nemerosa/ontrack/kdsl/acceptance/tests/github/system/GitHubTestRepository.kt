package net.nemerosa.ontrack.kdsl.acceptance.tests.github.system

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.gitHubPlaygroundClient
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.gitHubPlaygroundEnv
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.Ontrack
import net.nemerosa.ontrack.kdsl.spec.Project
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
            "private" to true,
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
        gitHubPlaygroundClient.delete(
            "/repos/${gitHubPlaygroundEnv.organization}/$repo"
        )
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
        TODO()
    }

    fun assertThatGitHubRepository(
        code: AssertionContext.() -> Unit,
    ) {
        val context = AssertionContext()
        context.code()
    }

    inner class AssertionContext {
        fun hasPR(from: String, to: String) {
            TODO("Not yet implemented")
        }

        fun fileContains(
            path: String,
            branch: String = "main",
            content: () -> String,
        ) {
            TODO("Not yet implemented")
        }

    }

}

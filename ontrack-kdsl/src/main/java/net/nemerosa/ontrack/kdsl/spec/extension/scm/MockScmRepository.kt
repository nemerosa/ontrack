package net.nemerosa.ontrack.kdsl.spec.extension.scm

import net.nemerosa.ontrack.kdsl.connector.parse
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.Build
import net.nemerosa.ontrack.kdsl.spec.Ontrack
import net.nemerosa.ontrack.kdsl.spec.Project
import org.springframework.web.client.HttpClientErrorException.NotFound
import java.util.*

/**
 * Client for the mock SCM, whose repositories, commits and issues are registered over REST
 * rather than through GraphQL — see `MockSCMController` on the server side.
 *
 * It lives here, in the KDSL, rather than in the acceptance tests, because the demo seed
 * needs it too: registering the commits behind the demo's change log is the same set of
 * calls. The assertions written on top of it stay in the acceptance tests, which is where
 * their timeouts and their JUnit failures belong.
 *
 * The mock SCM is off unless `ontrack.config.extension.scm.mock.enabled` is set, so calls
 * made through this client answer with a 404 on an instance that has not enabled it.
 */
fun <T> withMockScmRepository(
    ontrack: Ontrack,
    prefix: String = "ontrack-auto-versioning-test",
    code: MockScmRepositoryContext.() -> T,
): T {
    // Unique name for the repository
    val uuid = UUID.randomUUID().toString()
    val repo = "${prefix}-$uuid"

    // Context
    val context = MockScmRepositoryContext(ontrack, repo)

    // Running the code
    return context.code()
}

class MockScmRepositoryContext(
    private val ontrack: Ontrack,
    private val repository: String,
) {

    fun repositoryFile(
        path: String,
        branch: String = "main",
        content: () -> String,
    ) {
        ontrack.connector.post(
            "/extension/scm/mock/file",
            body = mapOf(
                "name" to repository,
                "scmBranch" to branch,
                "path" to path,
                "content" to content(),
            )
        )
    }

    /**
     * Declaring a new issue in the linked mock issue service.
     *
     * @param type Issue type, which is what a change log groups its issues by. Optional:
     * an issue with no type lands in the change log's untyped group.
     */
    fun repositoryIssue(key: String, message: String, type: String? = null) {
        ontrack.connector.post(
            "/extension/scm/mock/issue",
            body = mapOf(
                "name" to repository,
                "key" to key,
                "message" to message,
                "type" to type,
            )
        )
    }

    /**
     * Deletes the repository and everything it holds.
     *
     * The mock SCM keeps its repositories on the server, where they outlive the entities
     * pointing at them, so anything re-registering the same commits starts here.
     */
    fun deleteRepository() {
        ontrack.connector.delete("/extension/scm/mock/repository?repository=$repository")
    }

    /**
     * Registering a commit in the repository.
     *
     * @return The id of the registered commit, deterministic for a given branch and
     * position on it: `<branch>-<index>-<sha1 prefix>`.
     */
    fun repositoryCommit(
        message: String,
        branch: String = "main",
    ): String {
        return ontrack.connector.post(
            "/extension/scm/mock/commit",
            body = mapOf(
                "name" to repository,
                "scmBranch" to branch,
                "message" to message,
            )
        ).body.asJson().path("commitId").asText()
    }

    /**
     * Registering a commit in the repository and declaring it for the current build.
     */
    fun Build.withRepositoryCommit(message: String, property: Boolean = true): Build {
        // Declaring the commit first
        val commitId = repositoryCommit(message)
        // Setting the Git commit property for this build
        if (property) {
            // We're using the MOCK SCM, so a MOCK SCM Commit property must be set instead
            mockScmBuildCommitProperty = commitId
        }
        // OK
        return this
    }

    fun Project.configuredForMockScm() {
        mockScmProjectProperty = repository
    }

    fun Branch.configuredForMockRepository(
        scmBranch: String = "main",
    ) {
        project.configuredForMockScm()
        mockScmBranchProperty = scmBranch
    }

    fun getPR(from: String?, to: String?): MockSCMPullRequest? {
        var url = "/extension/scm/mock/pr?repository=$repository"
        if (from != null) {
            url += "&from=$from"
        }
        if (to != null) {
            url += "&to=$to"
        }
        return try {
            ontrack.connector.get(url).body.parse<MockSCMPullRequest>()
        } catch (_: NotFound) {
            null
        }
    }

    fun getFile(path: String, branch: String): String? =
        try {
            ontrack.connector.get("/extension/scm/mock/file?repository=$repository&scmBranch=$branch&path=$path")
                .body.parse<MockSCMFileContent>()
                .text
        } catch (_: NotFound) {
            null
        }

    fun getBranch(branch: String): MockSCMBranch? =
        try {
            ontrack.connector.get("/extension/scm/mock/branch?repository=$repository&scmBranch=$branch").body.parse()
        } catch (_: NotFound) {
            null
        }

    data class MockSCMPullRequest(
        val from: String,
        val to: String,
        val id: Int,
        val title: String,
        val body: String,
        val approved: Boolean,
        val status: String,
        val reviewers: List<String>,
    )

    data class MockSCMFileContent(
        val text: String,
    )

    data class MockSCMBranch(
        val name: String,
    )

}

package net.nemerosa.ontrack.kdsl.acceptance.tests.scm

import net.nemerosa.ontrack.kdsl.acceptance.tests.ACCProperties
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.connector.parse
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.Ontrack
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.extension.scm.mockScmBranchProperty
import net.nemerosa.ontrack.kdsl.spec.extension.scm.mockScmProjectProperty
import org.springframework.web.client.HttpClientErrorException.NotFound
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.fail

fun withMockScmRepository(
    ontrack: Ontrack,
    code: MockScmRepositoryContext.() -> Unit,
) {
    // Unique name for the repository
    val uuid = UUID.randomUUID().toString()
    val repo = "ontrack-auto-versioning-test-$uuid"

    // Context
    val context = MockScmRepositoryContext(ontrack, repo)

    // Running the code
    context.code()
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

    fun Project.configuredForMockScm() {
        mockScmProjectProperty = repository
    }

    fun Branch.configuredForMockRepository(
        scmBranch: String = "main",
    ) {
        project.configuredForMockScm()
        mockScmBranchProperty = scmBranch
    }

    fun assertThatMockScmRepository(
        code: AssertionContext.() -> Unit,
    ) {
        val context = AssertionContext()
        context.code()
    }

    private fun getPR(from: String?, to: String?): MockSCMPullRequest? {
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

    private fun getFile(path: String, branch: String): String? =
        try {
            ontrack.connector.get("/extension/scm/mock/file?repository=$repository&scmBranch=$branch&path=$path")
                .body.parse<MockSCMFileContent>()
                .text
        } catch (_: NotFound) {
            null
        }

    private fun getBranch(branch: String): MockSCMBranch? =
        try {
            ontrack.connector.get("/extension/scm/mock/branch?repository=$repository&scmBranch=$branch").body.parse()
        } catch (_: NotFound) {
            null
        }

    inner class AssertionContext {

        fun hasNoBranch(branch: String) {
            val gitHubBranch = getBranch(branch)
            if (gitHubBranch != null) {
                fail("Branch $branch exists when it was expected not to.")
            }
        }

        private fun checkPR(
            from: String?,
            to: String?,
            checkPR: (MockSCMPullRequest?) -> Boolean
        ): MockSCMPullRequest? {
            var pr: MockSCMPullRequest? = null
            waitUntil(
                task = "Checking the PR from $from to $to",
                timeout = ACCProperties.MockSCM.Timeouts.general,
                interval = ACCProperties.MockSCM.Timeouts.interval,
            ) {
                pr = getPR(from, to)
                checkPR(pr)
            }
            return pr
        }

        fun hasPR(from: String, to: String): MockSCMPullRequest =
            checkPR(from = from, to = to) {
                it != null
            } ?: error("PR cannot be null after the check")

        fun hasNoPR(to: String) {
            checkPR(from = null, to = to) {
                it == null
            }
        }

        fun checkPRIsNotApproved(pr: MockSCMPullRequest) {
            assertFalse(pr.approved, "PR ${pr.id} is not approved")
        }

        fun fileContains(
            path: String,
            branch: String = "main",
            timeout: Long = ACCProperties.MockSCM.Timeouts.general,
            content: () -> String,
        ) {
            val expectedContent = content()
            var actualContent: String?
            waitUntil(
                timeout = timeout,
                interval = ACCProperties.MockSCM.Timeouts.interval,
                task = "Waiting for file $path on branch $branch to have a given content.",
                onTimeout = {
                    actualContent = getFile(path, branch)
                    fail(
                        """
Expected the following content for the $path file on the $branch branch:

$expectedContent

but got:

$actualContent
""".trimIndent()
                    )
                }
            ) {
                actualContent = getFile(path, branch)
                actualContent?.contains(expectedContent) ?: false
            }
        }

    }

    data class MockSCMPullRequest(
        val from: String,
        val to: String,
        val id: Int,
        val title: String,
        val approved: Boolean,
        val merged: Boolean,
    )

    data class MockSCMFileContent(
        val text: String,
    )

    data class MockSCMBranch(
        val name: String,
    )

}

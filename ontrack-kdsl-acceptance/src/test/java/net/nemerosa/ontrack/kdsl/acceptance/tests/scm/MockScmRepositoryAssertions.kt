package net.nemerosa.ontrack.kdsl.acceptance.tests.scm

import net.nemerosa.ontrack.kdsl.acceptance.tests.ACCProperties
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.scm.MockScmRepositoryContext
import net.nemerosa.ontrack.kdsl.spec.extension.scm.MockScmRepositoryContext.MockSCMPullRequest
import kotlin.test.assertFalse
import kotlin.test.fail

/**
 * Assertions on the state of a mock SCM repository.
 *
 * The client itself is [MockScmRepositoryContext], in the KDSL, because the demo seed needs
 * it as well. What stays here is what only a test wants: polling with the acceptance
 * suite's timeouts, and failing the test when the repository never reaches the expected
 * state.
 */
fun MockScmRepositoryContext.assertThatMockScmRepository(
    code: AssertionContext.() -> Unit,
) {
    val context = AssertionContext(this)
    context.code()
}

class AssertionContext(
    private val repository: MockScmRepositoryContext,
) {

    fun hasNoBranch(branch: String) {
        val scmBranch = repository.getBranch(branch)
        if (scmBranch != null) {
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
            pr = repository.getPR(from, to)
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
                actualContent = repository.getFile(path, branch)
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
            actualContent = repository.getFile(path, branch)
            actualContent?.contains(expectedContent) ?: false
        }
    }

}

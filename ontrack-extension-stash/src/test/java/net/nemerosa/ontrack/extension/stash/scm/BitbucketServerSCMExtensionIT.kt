package net.nemerosa.ontrack.extension.stash.scm

import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.extension.stash.AbstractBitbucketTestSupport
import net.nemerosa.ontrack.extension.stash.TestOnBitbucketServer
import net.nemerosa.ontrack.extension.stash.bitbucketServerEnv
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

@TestOnBitbucketServer
class BitbucketServerSCMExtensionIT : AbstractBitbucketTestSupport() {

    @Autowired
    private lateinit var scmDetector: SCMDetector

    @Test
    fun `Project not configured does not have a BB Server SCM`() {
        project {
            val scm = scmDetector.getSCM(this)
            assertNull(scm, "No SCM for project")
        }
    }

    @Test
    fun `Project configured has a BB Server SCM`() {
        project {
            bitbucketServerConfig()
            val scm = scmDetector.getSCM(this)
            assertNotNull(scm, "SCM for project") {
                assertEquals(
                    "${bitbucketServerEnv.url}/projects/${bitbucketServerEnv.project}/repos/${bitbucketServerEnv.repository}",
                    it.repositoryHtmlURL
                )
            }
        }
    }

    @Test
    fun `Creating a branch`() {
        withScm { scm ->
            val branchName = uid("branch/")
            val commit = scm.createBranch(bitbucketServerEnv.defaultBranch, branchName)
            assertTrue(commit.isNotBlank(), "Commit returned")
        }
    }

    @Test
    fun `Editing and downloading a file on a branch`() {
        withScm { scm ->
            val branchName = uid("branch/")
            val fileName = uid("file_")
            val fileContent = uid("content_")
            scm.createBranch(bitbucketServerEnv.defaultBranch, branchName)
            // Creating the file
            scm.upload(
                scmBranch = branchName,
                commit = "",
                path = fileName,
                content = fileContent.encodeToByteArray(),
                message = "This is a test"
            )
            // Downloading the file
            val content = scm.download(
                scmBranch = branchName,
                path = fileName,
            )
            // Checks the content
            assertNotNull(content, "File content") {
                assertEquals(fileContent, it.decodeToString())
            }
        }
    }

    @Test
    fun `Creating a PR without auto approval`() {
        withPR(autoApproval = false) { pr ->
            assertFalse(pr.merged, "PR not merged")
        }
    }

    @Test
    fun `Creating a PR with auto approval`() {
        withPR(autoApproval = true) { pr ->
            assertTrue(pr.merged, "PR is merged")
        }
    }

    private fun withPR(
        autoApproval: Boolean,
        remoteAutoMerge: Boolean = false,
        code: (SCMPullRequest) -> Unit,
    ) {
        withScm { scm ->
            val commonName = uid("branch-")
            val baseName = "base/$commonName"
            val headName = "head/$commonName"
            val fileName = uid("file_")
            val fileContent = uid("content_")
            val newFileContent = uid("content_new_")
            // Base branch
            val baseCommit = scm.createBranch(bitbucketServerEnv.defaultBranch, baseName)
            // Initial file on the base
            scm.upload(
                scmBranch = baseName,
                commit = "",
                path = fileName,
                content = fileContent.encodeToByteArray(),
                message = "Initial file",
            )
            // Creating the head branch
            val headCommit = scm.createBranch(baseName, headName)
            // Updating the file on this branch
            scm.upload(
                scmBranch = headName,
                commit = headCommit,
                path = fileName,
                content = newFileContent.encodeToByteArray(),
                message = "Changed file",
            )
            // Creating the PR without auto merge & approval
            val pr = scm.createPR(
                from = headName,
                to = baseName,
                title = "Sample pull request",
                description = "Sample pull request description",
                autoApproval = autoApproval,
                remoteAutoMerge = remoteAutoMerge,
                message = "Commit message for auto merge"
            )
            // Checks
            assertTrue(pr.id.isNotBlank(), "PR created")
            code(pr)
        }
    }

    private fun withScm(code: (SCM) -> Unit) {
        project {
            bitbucketServerConfig()
            val scm = scmDetector.getSCM(this) ?: fail("No SCM for project")
            code(scm)
        }
    }

}
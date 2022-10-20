package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.stereotype.Component

@Component
class TestSCMExtension(
    extensionFeature: SCMExtensionFeature,
) : AbstractExtension(extensionFeature), SCMExtension {

    private val projects = mutableMapOf<String, TestSCMExtensionProject>()

    fun registerProjectForTestSCM(project: Project, init: TestSCMExtensionInitContext.() -> Unit) {
        val context = TestSCMExtensionInitContext()
        context.init()
        projects[project.name] = context.createConfig()
    }

    override fun getSCM(project: Project): SCM? =
        projects[project.name]?.let {
            TestSCM(project, it)
        }

    private inner class TestSCM(
        project: Project,
        private val config: TestSCMExtensionProject,
    ) : SCM {

        override val repositoryURI: String = "uri://test/${project.name}"

        override val repositoryHtmlURL: String = "uri://test/${project.name}.html"
        override val repository: String = project.name

        override fun getSCMBranch(branch: Branch): String = branch.name

        override fun createBranch(sourceBranch: String, newBranch: String): String =
            uid("$newBranch-commit-")

        override fun download(scmBranch: String, path: String, retryOnNotFound: Boolean): ByteArray? {
            val file = config.files.find {
                it.branch == scmBranch && it.path == path
            }
            return file?.content?.invoke()?.toByteArray()
        }

        override fun upload(scmBranch: String, commit: String, path: String, content: ByteArray) {}

        override fun createPR(
            from: String,
            to: String,
            title: String,
            description: String,
            autoApproval: Boolean,
            remoteAutoMerge: Boolean,
        ): SCMPullRequest {
            TODO("Not yet implemented")
        }

    }

    class TestSCMExtensionFile(
        val path: String,
        val branch: String,
        val content: () -> String?,
    )

    class TestSCMExtensionProject(
        val files: List<TestSCMExtensionFile>,
    )

    class TestSCMExtensionInitContext {

        private val files = mutableListOf<TestSCMExtensionFile>()

        fun withFile(path: String, branch: String = "main", content: () -> String?) {
            files += TestSCMExtensionFile(path, branch, content)
        }

        fun createConfig(): TestSCMExtensionProject =
            TestSCMExtensionProject(
                files
            )
    }

}
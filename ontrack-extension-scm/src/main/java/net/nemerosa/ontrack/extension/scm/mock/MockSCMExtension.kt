package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.scm.service.SCMExtension
import net.nemerosa.ontrack.extension.scm.service.SCMPath
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.ACC, RunProfile.UNIT_TEST)
class MockSCMExtension(
    extensionFeature: SCMExtensionFeature,
    private val propertyService: PropertyService,
) : AbstractExtension(extensionFeature), SCMExtension {

    override fun getSCM(project: Project): SCM? =
        propertyService.getPropertyValue(project, MockSCMProjectPropertyType::class.java)?.let {
            MockSCM(it)
        }

    override val type: String = "mock"

    override fun getSCMPath(configName: String, ref: String): SCMPath? {
        TODO("Not yet implemented")
    }

    private val repositories = mutableMapOf<String, MockRepository>()

    fun repository(name: String) = repositories.getOrPut(name) {
        MockRepository(name)
    }

    data class MockPullRequest(
        val from: String,
        val to: String,
        val id: Int,
        val title: String,
        val approved: Boolean,
        val merged: Boolean,
        val reviewers: List<String>,
    )

    data class MockBranch(
        val name: String,
    )

    class MockRepository(
        val name: String,
    ) {

        private val files = mutableMapOf<String, MutableMap<String, String>>()
        private val createdBranches = mutableMapOf<String, String>()
        private val createdPullRequests = mutableListOf<MockPullRequest>()

        fun registerFile(scmBranch: String, path: String, content: String) {
            val branch = files.getOrPut(scmBranch) {
                mutableMapOf()
            }
            branch[path] = content
        }

        fun getFile(scmBranch: String?, path: String) = files[scmBranch ?: ""]?.get(path)

        fun createBranch(sourceBranch: String, newBranch: String): String {
            createdBranches[sourceBranch] = newBranch
            return newBranch
        }

        fun createPR(
            from: String,
            to: String,
            title: String,
            autoApproval: Boolean,
            remoteAutoMerge: Boolean,
            reviewers: List<String>,
        ): SCMPullRequest {
            val id = createdPullRequests.size + 1
            val pr = MockPullRequest(
                from = from,
                to = to,
                id = id,
                title = title,
                approved = autoApproval,
                merged = autoApproval, // Merged immediately?
                reviewers = reviewers,
            )

            if (autoApproval) {
                // Merging the PR means that the BASE branch (to) must be adapted
                // Gets the files from the HEAD
                files[from]?.forEach { (path, content) ->
                    registerFile(to, path, content)
                }
            }

            createdPullRequests += pr
            return SCMPullRequest(
                id = id.toString(),
                name = "#$id",
                link = "mock:pr:$id",
                merged = pr.merged,
            )
        }

        fun findPR(from: String?, to: String?): MockPullRequest? =
            createdPullRequests
                .filter { from == null || it.from == from }.firstOrNull { to == null || it.to == to }

        fun getBranch(name: String): MockBranch? = createdBranches[name]?.let {
            MockBranch(it)
        }

    }

    private inner class MockSCM(
        private val mockScmProjectProperty: MockSCMProjectProperty,
    ) : SCM {

        override val repositoryURI: String = "mock:${mockScmProjectProperty.name}"

        override val repositoryHtmlURL: String = ""

        override val repository: String = mockScmProjectProperty.name

        override fun getSCMBranch(branch: Branch): String? =
            propertyService.getPropertyValue(branch, MockSCMBranchPropertyType::class.java)?.name

        override fun createBranch(sourceBranch: String, newBranch: String): String =
            repository(mockScmProjectProperty.name).createBranch(sourceBranch, newBranch)

        override fun download(scmBranch: String?, path: String, retryOnNotFound: Boolean): ByteArray? =
            repository(mockScmProjectProperty.name).getFile(scmBranch, path)?.toByteArray()

        override fun upload(scmBranch: String, commit: String, path: String, content: ByteArray, message: String) {
            repository(mockScmProjectProperty.name).registerFile(scmBranch, path, content.decodeToString())
        }

        override fun createPR(
            from: String,
            to: String,
            title: String,
            description: String,
            autoApproval: Boolean,
            remoteAutoMerge: Boolean,
            message: String,
            reviewers: List<String>,
        ): SCMPullRequest = repository(mockScmProjectProperty.name).createPR(
            from = from,
            to = to,
            title = title,
            autoApproval = autoApproval,
            remoteAutoMerge = remoteAutoMerge,
            reviewers = reviewers,
        )

    }
}
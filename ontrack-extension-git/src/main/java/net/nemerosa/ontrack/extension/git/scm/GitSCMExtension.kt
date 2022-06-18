package net.nemerosa.ontrack.extension.git.scm

import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.scm.service.SCMExtension
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

/**
 * Implementation of the [SCMExtension] for a Git project.
 *
 * WARNING: most of the operations are NOT possible.
 */
@Component
class GitSCMExtension(
    extensionFeature: GitExtensionFeature,
    private val propertyService: PropertyService,
) : AbstractExtension(extensionFeature), SCMExtension {

    override fun getSCM(project: Project): SCM? {
        val property: GitProjectConfigurationProperty? =
            propertyService.getProperty(project, GitProjectConfigurationPropertyType::class.java).value
        return property?.run {
            GitSCM(
                project,
                // property,
            )
        }
    }

    private inner class GitSCM(
        private val project: Project,
        // private val property: GitProjectConfigurationProperty,
    ) : SCM {

        override val repositoryURI: String
            get() = unsupported("repositoryURI")

        override fun getSCMBranch(branch: Branch): String? {
            checkProject(branch.project)
            val branchProperty: GitBranchConfigurationProperty? =
                propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java).value
            return branchProperty?.branch
        }

        override fun createBranch(sourceBranch: String, newBranch: String): String {
            unsupported("createBranch")
        }

        override fun download(scmBranch: String, path: String): ByteArray? {
            unsupported("download")
        }

        override fun upload(scmBranch: String, commit: String, path: String, content: ByteArray) {
            unsupported("upload")
        }

        override fun createPR(
            from: String,
            to: String,
            title: String,
            description: String,
            autoApproval: Boolean,
            remoteAutoMerge: Boolean,
        ): SCMPullRequest {
            unsupported("createPR")
        }

        private fun checkProject(other: Project) {
            check(other.id == project.id) {
                "An SCM service can be used only for its project."
            }
        }

        private fun unsupported(operation: String): Nothing =
            throw IllegalStateException(
                """
                    Operation [$operation] not supported by the Git SCM.
                    
                    The GitSCM will be removed in version 5 on Ontrack.
                """.trimIndent()
            )

    }

}
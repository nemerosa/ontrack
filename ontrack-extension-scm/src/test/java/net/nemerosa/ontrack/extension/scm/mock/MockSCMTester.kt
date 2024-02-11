package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.stereotype.Component
import kotlin.test.fail

@Component
class MockSCMTester(
    private val mockSCMExtension: MockSCMExtension,
    private val propertyService: PropertyService,
) {

    fun withMockSCMRepository(
        name: String = uid(""),
        code: MockSCMRepositoryContext.() -> Unit,
    ) {
        val context = MockSCMRepositoryContext(name)
        context.code()
    }

    inner class MockSCMRepositoryContext(
        val repositoryName: String,
    ) {

        private fun Project.configureMockSCMProject(
            issueServiceIdentifier: String? = null
        ) {
            propertyService.editProperty(
                this,
                MockSCMProjectPropertyType::class.java,
                MockSCMProjectProperty(
                    name = this@MockSCMRepositoryContext.repositoryName,
                    issueServiceIdentifier = issueServiceIdentifier,
                )
            )
        }

        fun Branch.configureMockSCMBranch(
            scmBranch: String = "main",
            issueServiceIdentifier: String? = null,
        ) {
            project.configureMockSCMProject(
                issueServiceIdentifier = issueServiceIdentifier,
            )
            propertyService.editProperty(
                this,
                MockSCMBranchPropertyType::class.java,
                MockSCMBranchProperty(scmBranch)
            )
        }

        fun repositoryIssue(key: String, message: String, type: String? = null) {
            mockSCMExtension
                .repository(repositoryName)
                .registerIssue(
                    key = key,
                    message = message,
                    types = if (type.isNullOrBlank()) {
                        emptyArray()
                    } else {
                        arrayOf(type)
                    },
                )
        }

        fun Build.withRepositoryCommit(message: String, property: Boolean = true) {
            val branchProperty = propertyService.getPropertyValue(branch, MockSCMBranchPropertyType::class.java)
            if (branchProperty != null) {
                val id = mockSCMExtension.repository(repositoryName).registerCommit(
                    scmBranch = branchProperty.name,
                    message = message
                )
                if (property) {
                    propertyService.editProperty(
                        this,
                        MockSCMBuildCommitPropertyType::class.java,
                        MockSCMBuildCommitProperty(id)
                    )
                }
            } else {
                fail("Branch not configured for Mock SCM")
            }
        }

    }
}
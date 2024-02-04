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

        private fun Project.configureMockSCMProject() {
            propertyService.editProperty(
                this,
                MockSCMProjectPropertyType::class.java,
                MockSCMProjectProperty(this@MockSCMRepositoryContext.repositoryName)
            )
        }

        fun Branch.configureMockSCMBranch(
            scmBranch: String = "main"
        ) {
            project.configureMockSCMProject()
            propertyService.editProperty(
                this,
                MockSCMBranchPropertyType::class.java,
                MockSCMBranchProperty(scmBranch)
            )
        }

        fun repositoryIssue(key: String, message: String) {
            mockSCMExtension.repository(repositoryName).registerIssue(key, message)
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
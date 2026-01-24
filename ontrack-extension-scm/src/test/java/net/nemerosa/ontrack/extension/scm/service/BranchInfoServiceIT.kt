package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.mock.MockSCMBuildCommitProperty
import net.nemerosa.ontrack.extension.scm.mock.MockSCMBuildCommitPropertyType
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@AsAdminTest
class BranchInfoServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Autowired
    private lateinit var branchInfoService: BranchInfoService

    @Test
    fun `Branch info attached to a commit`() {
        mockSCMTester.withMockSCMRepository {
            project {
                branch {
                    configureMockSCMBranch()
                    val pl = promotionLevel()
                    build {

                        val commit = withRepositoryCommit("Commit 1")
                        setProperty(
                            this,
                            MockSCMBuildCommitPropertyType::class.java,
                            MockSCMBuildCommitProperty(commit)
                        )
                        val run = promote(pl)

                        val branchInfos = branchInfoService.getBranchInfos(
                            project = project,
                            commit = commit,
                        )

                        assertEquals(1, branchInfos.size)
                        val branchInfo = branchInfos.single()

                        assertEquals("Development", branchInfo.type)
                        assertEquals(1, branchInfo.branchInfoList.size)
                        val branchInfoItem = branchInfo.branchInfoList.single()
                        assertEquals(branch.name, branchInfoItem.branch.name)
                        assertEquals(this, branchInfoItem.firstBuild)
                        assertEquals(listOf(run), branchInfoItem.promotions)
                    }
                }
            }
        }
    }

}
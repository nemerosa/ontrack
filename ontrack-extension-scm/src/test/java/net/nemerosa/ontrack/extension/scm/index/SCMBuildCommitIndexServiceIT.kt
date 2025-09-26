package net.nemerosa.ontrack.extension.scm.index

import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.structure.Build
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SCMBuildCommitIndexServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Autowired
    private lateinit var scmBuildCommitIndexService: SCMBuildCommitIndexService

    @Test
    @AsAdminTest
    fun `Getting the earliest build after a commit`() {
        mockSCMTester.withMockSCMRepository {
            project {
                var commit: String? = null
                var build2: Build? = null
                val branch = branch {
                    configureMockSCMBranch()
                    repositoryIssue("ISS-1", "Issue 1")
                    repositoryIssue("ISS-2", "Issue 2")
                    repositoryIssue("ISS-3", "Issue 3")
                    build("1") {
                        withRepositoryCommit("ISS-1 Commit 1", property = false)
                        withRepositoryCommit("ISS-1 Commit 2", property = true)
                    }
                    build2 = build("2") {
                        commit = withRepositoryCommit("ISS-1 Commit 3", property = false)
                        withRepositoryCommit("ISS-1 Commit 4", property = true)
                    }
                    build("3") {
                        withRepositoryCommit("ISS-2 Commit 5", property = false)
                        withRepositoryCommit("ISS-2 Commit 6", property = true)
                        withRepositoryCommit("ISS-3 Commit 7", property = false)
                    }
                }

                val count = scmBuildCommitIndexService.indexBuildCommits(this)
                assertEquals(3, count, "3 commits have been indexed")

                val foundBuild = scmBuildCommitIndexService.findEarliestBuildAfterCommit(
                    branch = branch,
                    commit = commit!!,
                )
                assertEquals(build2, foundBuild)
            }
        }
    }

}
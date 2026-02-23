package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.extension.scm.SCMExtensionConfigProperties
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ScmSearchIndexServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Autowired
    private lateinit var scmSearchIndexService: ScmSearchIndexService

    @Autowired
    private lateinit var scmExtensionConfigProperties: SCMExtensionConfigProperties

    @Test
    @AsAdminTest
    fun `Indexation of SCM commits`() {
        mockSCMTester.withMockSCMRepository {
            project {
                configureMockSCMProject()
                (1..50).forEach { no -> repositoryCommit("Commit $no") }
                val oldBatchSize = scmExtensionConfigProperties.search.database.batchSize
                try {
                    scmExtensionConfigProperties.search.database.batchSize = 10
                    scmSearchIndexService.index(this)

                    // Check the indexed commits
                    assertEquals(
                        (1..10).map { no -> "Commit $no" },
                        scmSearchIndexService.getCommits(this, offset = 0, size = 500).pageItems.map { it.message },
                        "Initial indexation"
                    )

                    // Second indexing
                    scmSearchIndexService.index(this)

                    // Check the indexed commits
                    assertEquals(
                        (1..20).map { no -> "Commit $no" },
                        scmSearchIndexService.getCommits(this, offset = 0, size = 500).pageItems.map { it.message },
                        "Second indexation"
                    )

                    // Completing the indexation
                    repeat(4) { scmSearchIndexService.index(this) }

                    // Checking that a new indexation does not report any indexation
                    assertEquals(
                        0,
                        scmSearchIndexService.index(this),
                        "Completed indexations"
                    )

                } finally {
                    scmExtensionConfigProperties.search.database.batchSize = oldBatchSize
                }
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Indexation of SCM commits and issues`() {
        mockSCMTester.withMockSCMRepository {
            project {
                configureMockSCMProject()
                // Creating 50 commits, some of them referencing issues
                (1..50).forEach { no ->
                    val issueKey = if (no % 3 == 0) "ISS-$no" else null
                    if (issueKey != null) {
                        // Declares the issue in the repository (as in ScmIssueSearchExtensionIT)
                        repositoryIssue(key = issueKey, message = "Sample issue $no")
                        repositoryCommit("$issueKey Commit $no")
                    } else {
                        repositoryCommit("Commit $no")
                    }
                }
                val oldBatchSize = scmExtensionConfigProperties.search.database.batchSize
                try {
                    // Indexing all commits in one go
                    scmExtensionConfigProperties.search.database.batchSize = 100
                    scmSearchIndexService.index(this)

                    // Expected messages for the first page (newest to oldest)
                    val expectedMessages = (1..50).map { no ->
                        if (no % 3 == 0) "ISS-$no Commit $no" else "Commit $no"
                    }

                    // Check the indexed commits
                    assertEquals(
                        expectedMessages,
                        scmSearchIndexService.getCommits(this, offset = 0, size = 500).pageItems.map { it.message },
                        "Initial indexation"
                    )

                    // Checking the issues
                    val issue = scmSearchIndexService.findIssues("ISS-9").singleOrNull()
                    assertNotNull(issue) {
                        assertEquals(project.id(), it.projectId)
                        val lastCommit = scmSearchIndexService.getIssueLastCommit(project, it.key)
                        assertNotNull(lastCommit) {
                            assertEquals("ISS-9 Commit 9", it.message)
                        }
                    }

                } finally {
                    scmExtensionConfigProperties.search.database.batchSize = oldBatchSize
                }
            }
        }
    }

}
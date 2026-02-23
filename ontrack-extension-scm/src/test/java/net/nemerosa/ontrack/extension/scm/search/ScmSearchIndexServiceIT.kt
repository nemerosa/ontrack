package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.extension.scm.SCMExtensionConfigProperties
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

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

}
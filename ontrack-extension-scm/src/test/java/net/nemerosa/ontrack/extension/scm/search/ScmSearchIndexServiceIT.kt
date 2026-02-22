package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.extension.scm.SCMExtensionConfigProperties
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ScmSearchIndexServiceIT: AbstractDSLTestSupport() {

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
                val commitIds = (1..500).map { no -> repositoryCommit("Commit $no") }
                val oldBatchSize = scmExtensionConfigProperties.search.database.batchSize
                try {
                    scmExtensionConfigProperties.search.database.batchSize = 100
                    scmSearchIndexService.index(this)
                } finally {
                    scmExtensionConfigProperties.search.database.batchSize = oldBatchSize
                }
            }
        }
    }

}
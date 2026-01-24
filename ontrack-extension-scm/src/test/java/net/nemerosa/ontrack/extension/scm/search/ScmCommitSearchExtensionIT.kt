package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.extension.scm.mock.MockSCMBuildCommitProperty
import net.nemerosa.ontrack.extension.scm.mock.MockSCMBuildCommitPropertyType
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.structure.SearchIndexService
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.SearchService
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AsAdminTest
@TestPropertySource(
    properties = [
        "ontrack.config.search.index.immediate=true",
        "ontrack.config.search.index.logging=true",
        "ontrack.config.search.index.tracing=true",
    ]
)
class ScmCommitSearchExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Autowired
    private lateinit var scmCommitSearchExtension: ScmCommitSearchExtension

    @Autowired
    private lateinit var searchIndexService: SearchIndexService

    @Autowired
    private lateinit var searchService: SearchService

    @Test
    fun `Searching commits`() {
        mockSCMTester.withMockSCMRepository {
            project {
                branch {
                    configureMockSCMBranch()
                    build {

                        val commit = withRepositoryCommit("Commit 1")
                        setProperty(
                            this,
                            MockSCMBuildCommitPropertyType::class.java,
                            MockSCMBuildCommitProperty(commit)
                        )
                        searchIndexService.index(scmCommitSearchExtension)

                        val results = searchService.paginatedSearch(
                            SearchRequest(
                                token = commit,
                                type = ScmCommitSearchExtension.SCM_COMMIT_SEARCH_RESULT_TYPE,
                            )
                        )

                        val item = results.items.single().data?.get(SearchResult.SEARCH_RESULT_ITEM)
                        assertNotNull(item, "Result found") {
                            assertIs<ScmCommitSearchItem>(it) { si ->
                                assertEquals(commit, si.id)
                            }
                        }

                    }
                }
            }
        }
    }

}
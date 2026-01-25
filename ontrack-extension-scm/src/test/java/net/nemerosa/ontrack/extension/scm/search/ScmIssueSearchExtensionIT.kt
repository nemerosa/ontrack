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
class ScmIssueSearchExtensionIT: AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Autowired
    private lateinit var scmCommitSearchExtension: ScmCommitSearchExtension

    @Autowired
    private lateinit var searchIndexService: SearchIndexService

    @Autowired
    private lateinit var searchService: SearchService

    @Test
    fun `Searching issues`() {
        mockSCMTester.withMockSCMRepository {
            project {
                branch {
                    configureMockSCMBranch()
                    build {

                        val issueKey = "ISS-1234"
                        repositoryIssue(key = issueKey, message = "Sample issue")
                        val commit = withRepositoryCommit("$issueKey Commit 1")
                        setProperty(
                            this,
                            MockSCMBuildCommitPropertyType::class.java,
                            MockSCMBuildCommitProperty(commit)
                        )
                        searchIndexService.index(scmCommitSearchExtension) // This includes the indexation of issues!

                        val results = searchService.paginatedSearch(
                            SearchRequest(
                                token = issueKey,
                                type = ScmIssueSearchExtension.SCM_ISSUE_SEARCH_RESULT_TYPE,
                            )
                        )

                        assertEquals(1, results.items.size, "One result expected")
                        val item = results.items.single().data?.get(SearchResult.SEARCH_RESULT_ITEM)
                        assertNotNull(item, "Result found") {
                            assertIs<ScmIssueSearchItem>(it) { si ->
                                assertEquals(issueKey, si.displayKey)
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Searching issues with a hash as a prefix`() {
        mockSCMTester.withIssuePattern("(#(\\d+))") {
            mockSCMTester.withMockSCMRepository {
                project {
                    branch {
                        configureMockSCMBranch()
                        build {

                            val issueKey = "#1234"
                            repositoryIssue(key = issueKey, message = "Sample issue")
                            val commit = withRepositoryCommit("$issueKey Commit 1")
                            setProperty(
                                this,
                                MockSCMBuildCommitPropertyType::class.java,
                                MockSCMBuildCommitProperty(commit)
                            )
                            searchIndexService.index(scmCommitSearchExtension) // This includes the indexation of issues!

                            val results = searchService.paginatedSearch(
                                SearchRequest(
                                    token = issueKey,
                                    type = ScmIssueSearchExtension.SCM_ISSUE_SEARCH_RESULT_TYPE,
                                )
                            )

                            assertEquals(1, results.items.size, "One result expected")
                            val item = results.items.single().data?.get(SearchResult.SEARCH_RESULT_ITEM)
                            assertNotNull(item, "Result found") {
                                assertIs<ScmIssueSearchItem>(it) { si ->
                                    assertEquals(issueKey, si.displayKey)
                                }
                            }

                        }
                    }
                }
            }
        }
    }

}
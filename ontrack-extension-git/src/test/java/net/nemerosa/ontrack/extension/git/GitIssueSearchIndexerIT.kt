package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.model.structure.SearchRequest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GitIssueSearchIndexerIT : AbstractGitSearchTestSupport() {

    @Autowired
    private lateinit var gitIssueSearchExtension: GitIssueSearchExtension

    @Autowired
    private lateinit var gitCommitSearchExtension: GitCommitSearchExtension

    @Test
    fun `Looking for Git issues referred to in messages`() {
        createRepo {
            var i = 0
            commit(++i, "No issue")
            commit(++i, "Issue #1")
            commit(++i, "Issue #2 and #3")
            commit(++i, "Issue #1 again")
        } and { repo, _ ->
            val project = project {
                gitProject(repo)
            }
            // Indexation of issues (this goes through commit indexation)
            searchIndexService.index(gitCommitSearchExtension)
            // Looks for issue 1..3
            (1..3).forEach { no ->
                val results = searchService.paginatedSearch(SearchRequest("#$no", gitIssueSearchExtension.searchResultType.id)).items
                val result = results.find { it.title == "Issue #$no" }
                assertNotNull(result) {
                    assertEquals("Issue #$no", it.title)
                    assertEquals("Issue #$no found in project ${project.name}", it.description)
                }
            }
            // Issue 4 not found
            val results = searchService.search(SearchRequest("#4", gitIssueSearchExtension.searchResultType.id))
            assertTrue(results.none { "#4" in it.title })
        }
    }

}
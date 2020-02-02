package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.model.structure.SearchIndexService
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertNotNull

/**
 * Testing the search on Git commits.
 */
@TestPropertySource(
        properties = [
            "ontrack.config.search.engine=elasticsearch",
            "ontrack.config.search.index.immediate=true"
        ]
)
class GitCommitSearchExtensionIT : AbstractGitTestSupport() {

    @Autowired
    protected lateinit var searchIndexService: SearchIndexService

    @Autowired
    protected lateinit var searchService: SearchService

    @Autowired
    protected lateinit var gitCommitSearchExtension: GitCommitSearchExtension

    @Test
    fun `Looking for a commit on a project`() {
        createRepo {
            commits(10)
        } and { repo, commits ->
            project {
                gitProject(repo)
                // Re-indexes the commits
                searchIndexService.index(gitCommitSearchExtension)
                // Looks for every commit
                commits.forEach { (_, commit) ->
                    val results = searchService.search(SearchRequest(commit))
                    val title = "$name $commit"
                    val result = results.find { it.title == title }
                    assertNotNull(result)
                }
            }
        }
    }

}
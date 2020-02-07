package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.model.structure.SearchIndexService
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertTrue

/**
 * Testing the search on Git branches.
 */
@TestPropertySource(
        properties = [
            "ontrack.config.search.engine=elasticsearch",
            "ontrack.config.search.index.immediate=true"
        ]
)
class GitBranchSearchIndexerIT : AbstractGitTestSupport() {

    @Autowired
    protected lateinit var searchIndexService: SearchIndexService

    @Autowired
    protected lateinit var searchService: SearchService

    @Autowired
    protected lateinit var gitBranchSearchIndexer: GitBranchSearchIndexer

    @Test
    fun `Looking for a Git branch`() {
        createRepo {
            commits(1)
        } and { repo, commits ->
            project {
                gitProject(repo)
                val branch = branch {
                    gitBranch("release/1.0")
                    // Re-indexes the commits
                    searchIndexService.index(gitBranchSearchIndexer)
                }
                // Looks for the branch
                val results = searchService.search(SearchRequest("release/1.0", gitBranchSearchIndexer.searchResultType.id))
                assertTrue(results.any { it.title == branch.entityDisplayName }, "Branch found from Git branch")
            }
        }
    }

    @Test
    fun `Looking for a Git branch after the Ontrack branch is deleted`() {
        createRepo {
            commits(1)
        } and { repo, commits ->
            project {
                gitProject(repo)
                val branch = branch {
                    gitBranch("release/1.0")
                    // Re-indexes the commits
                    searchIndexService.index(gitBranchSearchIndexer)
                }
                // Looks for the branch
                val results = searchService.search(SearchRequest("release/1.0", gitBranchSearchIndexer.searchResultType.id))
                assertTrue(results.any { it.title == branch.entityDisplayName }, "Branch found from Git branch")
                // Deletes the branch
                branch.delete()
                // Looks for the branch again
                val newResults = searchService.search(SearchRequest("release/1.0", gitBranchSearchIndexer.searchResultType.id))
                assertTrue(newResults.none { it.title == branch.entityDisplayName }, "Branch not found from Git branch")
            }
        }
    }

    @Test
    fun `Looking for a Git branch just after it has been assigned`() {
        createRepo {
            commits(1)
        } and { repo, commits ->
            project {
                gitProject(repo)
                val branch = branch {
                    gitBranch("release/1.0")
                }
                // Looks for the branch
                val results = searchService.search(SearchRequest("release/1.0", gitBranchSearchIndexer.searchResultType.id))
                assertTrue(results.any { it.title == branch.entityDisplayName }, "Branch found from Git branch")
            }
        }
    }

    @Test
    fun `Looking for a Git branch just after it has been unassigned`() {
        createRepo {
            commits(1)
        } and { repo, commits ->
            project {
                gitProject(repo)
                val branch = branch {
                    gitBranch("release/1.0")
                }
                // Looks for the branch
                val results = searchService.search(SearchRequest("release/1.0", gitBranchSearchIndexer.searchResultType.id))
                assertTrue(results.any { it.title == branch.entityDisplayName }, "Branch found from Git branch")
                // Now, removes the Git branch
                deleteProperty(branch, GitBranchConfigurationPropertyType::class.java)
                // Looks for the branch again
                val newResults = searchService.search(SearchRequest("release/1.0", gitBranchSearchIndexer.searchResultType.id))
                assertTrue(newResults.none { it.title == branch.entityDisplayName }, "Branch not found from Git branch")
            }
        }
    }


}
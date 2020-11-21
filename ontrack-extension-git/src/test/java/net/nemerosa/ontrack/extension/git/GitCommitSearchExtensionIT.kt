package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.model.structure.SearchIndexService
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Testing the search on Git commits.
 */
class GitCommitSearchExtensionIT : AbstractGitSearchTestSupport() {

    @Autowired
    protected lateinit var gitCommitSearchExtension: GitCommitSearchExtension

    @Test
    fun `Looking for a commit on a project when Git repo is not indexed yet`() {
        createRepo {
            commits(10)
        } and { repo, commits ->
            project {
                gitProject(repo, sync = false)
                // Re-indexes the commits
                searchIndexService.index(gitCommitSearchExtension)
                // Looks for every commit, they must not be found
                commits.forEach { (_, commit) ->
                    val results = searchService.paginatedSearch(SearchRequest(commit)).items
                    val title = "$name $commit"
                    val result = results.find { it.title == title }
                    assertNull(result, "No commit is indexed yet")
                }
            }
        }
    }

    @Test
    fun `Looking for a commit on a project when Git repo has been removed`() {
        createRepo {
            commits(10)
        } and { repo, commits ->
            project {
                gitProject(repo, sync = false)
                // Destroys the repository
                repo.close()
                // Re-indexes the commits
                searchIndexService.index(gitCommitSearchExtension)
                // Looks for every commit, they must not be found
                commits.forEach { (_, commit) ->
                    val results = searchService.paginatedSearch(SearchRequest(commit)).items
                    val title = "$name $commit"
                    val result = results.find { it.title == title }
                    assertNull(result, "No commit is indexed yet")
                }
            }
        }
    }

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
                    val results = searchService.paginatedSearch(SearchRequest(commit)).items
                    val title = "$name $commit"
                    val result = results.find { it.title == title }
                    assertNotNull(result)
                }
            }
        }
    }

    @Test
    fun `Looking for a commit on a project using its short ID`() {
        createRepo {
            commits(10, shortId = true)
        } and { repo, commits ->
            project {
                gitProject(repo)
                // Re-indexes the commits
                searchIndexService.index(gitCommitSearchExtension)
                // Looks for every commit
                commits.forEach { (_, commit) ->
                    val results = searchService.paginatedSearch(SearchRequest(commit)).items
                    val title = "$name $commit"
                    val result = results.find { it.title.startsWith(title) }
                    assertNotNull(result)
                }
            }
        }
    }

    @Test
    fun `Looking for a commit on a project after its has been deleted`() {
        createRepo {
            commits(10)
        } and { repo, commits ->
            val project = project {
                gitProject(repo)
            }
            asAdmin {
                // Re-indexes the commits
                searchIndexService.index(gitCommitSearchExtension)
                // Deletes the project
                structureService.deleteProject(project.id)
            }
            // Looks for every commit
            commits.forEach { (_, commit) ->
                val results = searchService.paginatedSearch(SearchRequest(commit)).items
                val title = "${project.name} $commit"
                val result = results.find { it.title == title }
                assertNull(result, "Cannot find commit after project is deleted")
            }
        }
    }

}
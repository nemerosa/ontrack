package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.extension.git.AbstractGitSearchTestSupport
import net.nemerosa.ontrack.extension.git.GitCommitSearchExtension
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ProjectGitCommitInfoGraphQLIT : AbstractGitSearchTestSupport() {

    @Autowired
    protected lateinit var gitCommitSearchExtension: GitCommitSearchExtension

    @Test
    @AsAdminTest
    fun `Getting the basic commit info for a project`() {
        createRepo {
            commits(2)
        } and { repo, commits ->
            project {
                gitProject(repo)
                // Re-indexes the commits
                searchIndexService.index(gitCommitSearchExtension)
                // Looking for the Git commit info
                run(
                    """
                        project(id: $id) {
                            gitCommitInfo(commit: "${commits[1]}") {
                                scmCommit {
                                    id
                                }
                            }
                        }
                    """.trimIndent()
                )
            }
        }
    }

}
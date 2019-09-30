package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GitBranchDisplayNameExtensionIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var branchDisplayNameService: BranchDisplayNameService

    @Test
    fun `Using the Git branch as display name`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                gitProject(repo)
                branch("release-1.0") {
                    gitBranch("release/1.0")
                    assertEquals(
                            "release/1.0",
                            branchDisplayNameService.getBranchDisplayName(this)
                    )
                }
            }
        }
    }

}
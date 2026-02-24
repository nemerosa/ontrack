package net.nemerosa.ontrack.extension.github.scm

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.TestOnGitHub
import net.nemerosa.ontrack.extension.scm.search.ScmSearchIndexService
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

@TestOnGitHub
class GitHubSCMExtensionIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var scmSearchIndexService: ScmSearchIndexService

    @Test
    @AsAdminTest
    fun `GitHub SCM for all commits`() {
        project {
            gitHubRealConfig()
            // Launching the indexation for this repository (twice)
            repeat(2) {
                scmSearchIndexService.index(this)
            }
            // Gets the list of commits
            val commits = scmSearchIndexService.getCommits(this, 0, 10).pageItems
            assertTrue(commits.isNotEmpty(), "At least one commit must be found")
        }
    }

}
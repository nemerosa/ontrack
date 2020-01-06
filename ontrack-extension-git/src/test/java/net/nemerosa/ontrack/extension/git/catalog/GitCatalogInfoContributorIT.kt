package net.nemerosa.ontrack.extension.git.catalog

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.scm.catalog.CatalogFixtures
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitCatalogInfoContributorIT: AbstractGitTestSupport() {

    @Autowired
    private lateinit var contributor: GitCatalogInfoContributor

    @Test
    fun `Git information`() {
        createRepo {
            commits(5)
        } and { repo, commits ->
            project {
                gitProject(repo, sync = true)
                // Creates a pseudo entry
                val entry = CatalogFixtures.entry()
                // Gets the Git info
                val info = contributor.collectInfo(this, entry)
                assertNotNull(info) {
                    it.uiCommit.apply {
                        assertEquals(commits[5], id)
                        assertEquals("Commit 5", message.trim())
                    }
                }
            }
        }
    }

}
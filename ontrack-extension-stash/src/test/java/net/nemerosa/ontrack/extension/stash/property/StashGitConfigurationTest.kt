package net.nemerosa.ontrack.extension.stash.property

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StashGitConfigurationTest {

    @Test
    fun bitbucketServer() {
        val gitConfiguration = StashGitConfiguration(
            configuration = StashConfiguration(
                "server",
                "https://stash.mycompany.com",
                "", "", null, null
            ),
            project = "nemerosa",
            repository = "ontrack",
            indexationInterval = 0,
            configuredIssueService = null,
        )
        assertFalse(gitConfiguration.isCloud)
        assertEquals(
            "https://stash.mycompany.com/projects/nemerosa/repos/ontrack/commits/{commit}",
            gitConfiguration.commitLink
        )
        assertEquals(
            "https://stash.mycompany.com/projects/nemerosa/repos/ontrack/browse/{path}?at={commit}",
            gitConfiguration.fileAtCommitLink
        )
        assertEquals("https://stash.mycompany.com/scm/nemerosa/ontrack.git", gitConfiguration.remote)
    }

    @Test
    fun bitbucketCloud() {
        val gitConfiguration = StashGitConfiguration(
            configuration = StashConfiguration(
                "cloud",
                "https://bitbucket.org",
                "", "", null, null
            ),
            project = "nemerosa",
            repository = "ontrack",
            indexationInterval = 0,
            configuredIssueService = null,
        )
        assertTrue(gitConfiguration.isCloud)
        assertEquals("https://bitbucket.org/nemerosa/ontrack/commits/{commit}", gitConfiguration.commitLink)
        assertEquals(
            "https://bitbucket.org/nemerosa/ontrack/src/{commit}/{path}",
            gitConfiguration.fileAtCommitLink
        )
        assertEquals("https://bitbucket.org/nemerosa/ontrack.git", gitConfiguration.remote)
    }
}

package net.nemerosa.ontrack.extension.stash.property

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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

}

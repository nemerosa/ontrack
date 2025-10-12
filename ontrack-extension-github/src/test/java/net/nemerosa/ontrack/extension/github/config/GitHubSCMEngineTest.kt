package net.nemerosa.ontrack.extension.github.config

import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GitHubSCMEngineTest {

    private val engine = GitHubSCMEngine(
        propertyService = mockk(),
    )

    @Test
    fun `Detection of GitHub SCM engine`() {
        assertFalse(engine.matchesUrl("https://bitbucket.dev.yontrack.com/project/NEMEROSA/repository/YONTRACK"))
        assertTrue(engine.matchesUrl("https://github.com/nemerosa/yontrack.git"))
        assertTrue(engine.matchesUrl("git@github.com:nemerosa/ontrack.git"))
    }

}
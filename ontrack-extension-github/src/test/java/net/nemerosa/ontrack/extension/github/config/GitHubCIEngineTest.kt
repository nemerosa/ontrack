package net.nemerosa.ontrack.extension.github.config

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GitHubCIEngineTest {

    @Test
    fun `GitHub CI engine`() {
        val engine = GitHubCIEngine()
        assertTrue(engine.matchesEnv(mapOf("GITHUB_ACTIONS" to "true")))
        assertFalse(engine.matchesEnv(mapOf("GITHUB_ACTIONS" to "false")))
        assertFalse(engine.matchesEnv(emptyMap()))
    }

}

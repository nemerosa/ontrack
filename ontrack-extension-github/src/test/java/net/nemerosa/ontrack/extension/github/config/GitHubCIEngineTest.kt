package net.nemerosa.ontrack.extension.github.config

import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GitHubCIEngineTest {

    @Test
    fun `GitHub CI engine`() {
        val engine = GitHubCIEngine(mockk())
        assertTrue(engine.matchesEnv(mapOf("GITHUB_ACTIONS" to "true")))
        assertFalse(engine.matchesEnv(mapOf("GITHUB_ACTIONS" to "false")))
        assertFalse(engine.matchesEnv(emptyMap()))
    }

    @Test
    fun `GitHub SCM URL`() {
        val engine = GitHubCIEngine(mockk())
        assertNull(engine.getScmUrl(emptyMap()))
        assertEquals(
            "https://github.com/nemerosa/ontrack.git",
            engine.getScmUrl(
                mapOf(
                    "GITHUB_SERVER_URL" to "https://github.com",
                    "GITHUB_REPOSITORY" to "nemerosa/ontrack",
                )
            )
        )
    }

}

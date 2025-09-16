package net.nemerosa.ontrack.extension.github.ingestion.settings

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GitHubIngestionSettingsTest {

    @Test
    fun obfuscation() {
        val settings = GitHubIngestionSettings(enabled = true, token = "secret")
        val obfuscated = settings.obfuscate()
        assertEquals("", obfuscated.token)
    }

}
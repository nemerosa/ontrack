package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettingsHelper.excludes
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettingsHelper.includes
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GitHubIngestionSettingsHelperTest {

    @Test
    fun `Inclusions and exclusions`() {
        assertTrue(includes("ontrack-pro", ".*", ""))
        assertTrue(includes("ontrack-pro", "ontrack.*", ""))
        assertTrue(includes("ontrack-pro", "ontrack.*|nemerosa.*", ""))
        assertFalse(includes("ontrack-pro", "ontrack.*|nemerosa.*", ".*pro.*"))
        assertFalse(includes("ontrack-pro", ".*", ".*"))
        assertFalse(includes("ontrack-pro", ".*", "ontrack-.*"))

        assertFalse(excludes("ontrack-pro", ".*", ""))
        assertFalse(excludes("ontrack-pro", "ontrack.*", ""))
        assertFalse(excludes("ontrack-pro", "ontrack.*|nemerosa.*", ""))
        assertTrue(excludes("ontrack-pro", "ontrack.*|nemerosa.*", ".*pro.*"))
        assertTrue(excludes("ontrack-pro", ".*", ".*"))
        assertTrue(excludes("ontrack-pro", ".*", "ontrack-.*"))
    }
}
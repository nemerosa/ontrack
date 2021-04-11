package net.nemerosa.ontrack.casc

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.settings.SecuritySettings
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class CascStartupIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var cascStartup: CascStartup

    @Autowired
    private lateinit var cascConfigurationProperties: CascConfigurationProperties

    @Test
    fun `Loading of one file`() {
        cascConfigurationProperties.apply {
            locations = listOf(
                "classpath:casc/settings.yaml"
            )
        }
        withSettings<SecuritySettings> {
            withNoGrantViewToAll {
                cascStartup.start()
            }
        }

        // Checks the new settings
        val settings = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
        assertTrue(settings.isGrantProjectViewToAll)
        assertTrue(settings.isGrantProjectParticipationToAll)
    }

}
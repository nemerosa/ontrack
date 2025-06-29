package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.extension.casc.support.SampleSettings
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.settings.SecuritySettings
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
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
                "classpath:casc/settings-security.yaml"
            )
        }
        withSettings<SecuritySettings> {
            withNoGrantViewToAll {
                cascStartup.start()
                // Checks the new settings
                val settings = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
                assertTrue(settings.isGrantProjectViewToAll)
                assertTrue(settings.isGrantProjectParticipationToAll)
            }
        }
    }

    @Test
    fun `Loading of two files`() {
        cascConfigurationProperties.apply {
            locations = listOf(
                "classpath:casc/settings-security.yaml",
                "classpath:casc/settings-sample.yaml",
            )
        }
        withSettings<SecuritySettings> {
            withSettings<SampleSettings> {
                withNoGrantViewToAll {
                    cascStartup.start()
                    // Checks the new settings
                    val securitySettings = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
                    assertTrue(securitySettings.isGrantProjectViewToAll)
                    assertTrue(securitySettings.isGrantProjectParticipationToAll)

                    val sampleSettings = cachedSettingsService.getCachedSettings(SampleSettings::class.java)
                    assertEquals(200, sampleSettings.maxProjects)
                    assertEquals(true, sampleSettings.enabled)
                }
            }
        }

    }

    @Test
    fun `Loading from a directory`() {
        cascConfigurationProperties.apply {
            locations = listOf(
                "classpath:casc/",
            )
        }
        withSettings<SecuritySettings> {
            withSettings<SampleSettings> {
                withNoGrantViewToAll {
                    cascStartup.start()
                    // Checks the new settings
                    val securitySettings = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
                    assertTrue(securitySettings.isGrantProjectViewToAll)
                    assertTrue(securitySettings.isGrantProjectParticipationToAll)

                    val sampleSettings = cachedSettingsService.getCachedSettings(SampleSettings::class.java)
                    assertEquals(200, sampleSettings.maxProjects)
                    assertEquals(true, sampleSettings.enabled)
                }
            }
        }

    }

}
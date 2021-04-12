package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.model.settings.SecuritySettings
import org.junit.Test
import kotlin.test.assertTrue

class CascSecuritySettingsIT: AbstractCascTestSupport() {

    @Test
    fun `Security settings`() {
        withSettings<SecuritySettings> {
            withNoGrantViewToAll {
                casc("""
                    ontrack:
                        config:
                            settings:
                                security:
                                    grantProjectViewToAll: true
                                    grantProjectParticipationToAll: true
                """.trimIndent())

                // Checks the new settings
                val settings = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
                assertTrue(settings.isGrantProjectViewToAll)
                assertTrue(settings.isGrantProjectParticipationToAll)
            }
        }
    }

}
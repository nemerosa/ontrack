package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.extension.casc.context.settings.SecuritySettingsContext
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.settings.SecuritySettings
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CascSecuritySettingsIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var securitySettingsContext: SecuritySettingsContext

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

    @Test
    fun `Rendering the settings`() {
        asAdmin {
            withSettings<SecuritySettings> {
                settingsManagerService.saveSettings(
                    SecuritySettings(
                        isGrantProjectViewToAll = true,
                        isGrantProjectParticipationToAll = true,
                    )
                )
                val json = securitySettingsContext.render()
                assertEquals(
                    mapOf(
                        "grantProjectViewToAll" to true,
                        "grantProjectParticipationToAll" to true,
                    ).asJson(),
                    json
                )
            }
        }
    }

}
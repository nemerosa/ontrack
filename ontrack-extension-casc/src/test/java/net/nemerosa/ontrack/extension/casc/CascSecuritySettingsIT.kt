package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.extension.casc.context.settings.SecuritySettingsContext
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.settings.SecuritySettings
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CascSecuritySettingsIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var securitySettingsContext: SecuritySettingsContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = securitySettingsContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "SecuritySettings",
                  "description": null,
                  "properties": {
                    "builtInAuthenticationEnabled": {
                      "description": "Enabling the built-in authentication",
                      "type": "boolean"
                    },
                    "grantDashboardEditionToAll": {
                      "description": "Grants dashboard creation rights to all",
                      "type": "boolean"
                    },
                    "grantDashboardSharingToAll": {
                      "description": "Grants dashboard sharing rights to all",
                      "type": "boolean"
                    },
                    "grantProjectParticipationToAll": {
                      "description": "Grants project participation to all",
                      "type": "boolean"
                    },
                    "grantProjectViewToAll": {
                      "description": "Grants project view to all",
                      "type": "boolean"
                    }
                  },
                  "required": [
                  ],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Security settings`() {
        withSettings<SecuritySettings> {
            withNoGrantViewToAll {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                security:
                                    grantProjectViewToAll: true
                                    grantProjectParticipationToAll: true
                """.trimIndent()
                )

                // Checks the new settings
                val settings = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
                assertTrue(settings.isGrantProjectViewToAll)
                assertTrue(settings.isGrantProjectParticipationToAll)
                assertTrue(settings.builtInAuthenticationEnabled)
            }
        }
    }

    @Test
    fun `Disabling dashboard rights`() {
        withCleanSettings<SecuritySettings> {

            val defaults = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
            assertEquals(true, defaults.grantDashboardEditionToAll)
            assertEquals(true, defaults.grantDashboardSharingToAll)

            casc(
                """
                    ontrack:
                        config:
                            settings:
                                security:
                                    grantDashboardEditionToAll: false
                                    grantDashboardSharingToAll: false
                """.trimIndent()
            )

            // Checks the new settings
            val settings = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
            assertEquals(false, settings.grantDashboardEditionToAll)
            assertEquals(false, settings.grantDashboardSharingToAll)
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
                        "builtInAuthenticationEnabled" to true,
                        "grantDashboardEditionToAll" to true,
                        "grantDashboardSharingToAll" to true,
                    ).asJson(),
                    json
                )
            }
        }
    }

    @Test
    fun `Disabling the built-in authentication`() {
        // We cannot use the `admin` user since built-in authentication will be disabled
        securityService.asAdmin {
            val old = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
            try {
                settingsManagerService.saveSettings(
                    SecuritySettings(
                        isGrantProjectViewToAll = true,
                        isGrantProjectParticipationToAll = true,
                        builtInAuthenticationEnabled = true,
                    )
                )
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                security:
                                    builtInAuthenticationEnabled: false
                """.trimIndent()
                )

                // Checks the new settings
                val settings = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
                assertTrue(settings.isGrantProjectViewToAll)
                assertTrue(settings.isGrantProjectParticipationToAll)
                assertFalse(settings.builtInAuthenticationEnabled)

            }
            // Restoring the old settings
            finally {
                settingsManagerService.saveSettings(old)
            }
        }
    }

}
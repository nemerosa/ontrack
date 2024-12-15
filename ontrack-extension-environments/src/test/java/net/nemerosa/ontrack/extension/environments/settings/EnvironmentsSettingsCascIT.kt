package net.nemerosa.ontrack.extension.environments.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class EnvironmentsSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var environmentsSettingsCasc: EnvironmentsSettingsCasc

    @Test
    fun `Environment settings`() {
        asAdmin {
            withCleanSettings<EnvironmentsSettings> {
                casc(
                    """
                        ontrack:
                            config:
                                settings:
                                    environments:
                                        buildDisplayOption: ALL
                    """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(EnvironmentsSettings::class.java)
                assertEquals(
                    EnvironmentsSettings(
                        buildDisplayOption = EnvironmentsSettingsBuildDisplayOption.ALL,
                    ),
                    settings
                )
            }
        }
    }

    @Test
    fun `Rendering the environment settings`() {
        asAdmin {
            withCleanSettings<EnvironmentsSettings> {
                settingsManagerService.saveSettings(
                    EnvironmentsSettings(
                        buildDisplayOption = EnvironmentsSettingsBuildDisplayOption.COUNT,
                    )
                )
                val json = environmentsSettingsCasc.render()
                assertEquals(
                    mapOf(
                        "buildDisplayOption" to "COUNT"
                    ).asJson(),
                    json
                )
            }
        }
    }

}
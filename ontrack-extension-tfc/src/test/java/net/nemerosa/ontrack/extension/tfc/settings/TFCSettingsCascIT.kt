package net.nemerosa.ontrack.extension.tfc.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TFCSettingsCascIT : AbstractCascTestSupport() {

    @Test
    fun `Minimal set of parameters`() {
        asAdmin {
            withCleanSettings<TFCSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                tfc:
                                    enabled: true
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(TFCSettings::class.java)
                assertEquals(true, settings.enabled)
            }
        }
    }

}
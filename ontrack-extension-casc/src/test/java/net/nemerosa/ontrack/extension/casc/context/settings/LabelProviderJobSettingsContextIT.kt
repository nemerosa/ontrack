package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.settings.LabelProviderJobSettings
import org.junit.Test
import kotlin.test.assertEquals

class LabelProviderJobSettingsContextIT : AbstractCascTestSupport() {

    @Test
    fun `Label provider job settings as CasC`() {
        asAdmin {
            withSettings<LabelProviderJobSettings> {
                casc("""
                    ontrack:
                        config:
                            settings:
                                label-provider-job:
                                    enabled: true
                                    interval: 120
                                    perProject: true
                """.trimIndent())
                val settings = cachedSettingsService.getCachedSettings(LabelProviderJobSettings::class.java)
                assertEquals(true, settings.enabled)
                assertEquals(120, settings.interval)
                assertEquals(true, settings.perProject)
            }
        }
    }

}
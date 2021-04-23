package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.Test
import kotlin.test.assertTrue

class PreviousPromotionConditionSettingsContextIT : AbstractCascTestSupport() {

    @Test
    fun `Previous promotion condition settings as CasC`() {
        asAdmin {
            withSettings<PreviousPromotionConditionSettings> {
                casc("""
                    ontrack:
                        config:
                            settings:
                                previous-promotion-condition:
                                    previousPromotionRequired: true
                """.trimIndent())
                val settings = cachedSettingsService.getCachedSettings(PreviousPromotionConditionSettings::class.java)
                assertTrue(settings.previousPromotionRequired)
            }
        }
    }

}
package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.extension.av.settings.AutoVersioningSettings
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNull

class AutoVersioningTrackingServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var autoVersioningTrackingService: AutoVersioningTrackingService

    @Test
    fun `No trail by default if auto-versioning is disabled`() {
        asAdmin {
            withCleanSettings<AutoVersioningSettings> {
                settingsManagerService.saveSettings(AutoVersioningSettings(enabled = false))
                withRun { run ->
                    assertNull(
                        autoVersioningTrackingService.getTrail(run),
                        "No trail by default"
                    )
                }
            }
        }
    }

    private fun withRun(code: (run: PromotionRun) -> Unit) {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    build {
                        val run = promote(pl)
                        code(run)
                    }
                }
            }
        }
    }

}
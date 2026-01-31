package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.settings.JobHistorySettings
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

@AsAdminTest
class JobHistorySettingsCascIT : AbstractCascTestSupport() {

    @Test
    fun `Job history settings`() {
        withSettings<JobHistorySettings> {
            casc(
                """
                    ontrack:
                        config:
                            settings:
                                job-history:
                                    retention: 60d
                """.trimIndent()
            )
            val settings = cachedSettingsService.getCachedSettings(JobHistorySettings::class.java)
            assertEquals(Duration.ofDays(60), settings.retention)
        }
    }

}
package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.settings.JobHistorySettings
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class JobHistorySettingsIT : AbstractDSLTestSupport() {

    @Test
    fun `Saving and retrieving the settings`() {
        asAdmin {
            withCleanSettings<JobHistorySettings> {
                val settings = JobHistorySettings(
                    retention = Duration.ofDays(10),
                )
                settingsManagerService.saveSettings(settings)
                val saved = settingsService.getCachedSettings(JobHistorySettings::class.java)
                assertEquals(settings, saved)
            }
        }
    }

    @Test
    fun `Default settings`() {
        asAdmin {
            withCleanSettings<JobHistorySettings> {
                val settings = settingsService.getCachedSettings(JobHistorySettings::class.java)
                assertEquals(JobHistorySettings.DEFAULT_JOB_HISTORY_RETENTION, settings.retention)
            }
        }
    }

}
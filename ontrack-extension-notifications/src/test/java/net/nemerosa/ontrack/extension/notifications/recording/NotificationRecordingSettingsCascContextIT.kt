package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class NotificationRecordingSettingsCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var settingsRepository: SettingsRepository

    @Test
    fun `Notification recording settings using CasC`() {
        withSettings<NotificationRecordingSettings> {
            settingsRepository.deleteAll(NotificationRecordingSettings::class.java)
            casc("""
                ontrack:
                    config:
                        settings:
                            notification-recordings:
                                enabled: true
                                retentionSeconds: 21600
                                cleanupIntervalSeconds: 3600
            """.trimIndent())
            val settings = settingsService.getCachedSettings(NotificationRecordingSettings::class.java)
            assertTrue(settings.enabled)
            assertEquals(21600, settings.retentionSeconds)
            assertEquals(3600, settings.cleanupIntervalSeconds)
        }
    }

}
package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var settingsRepository: SettingsRepository

    @Test
    fun `Slack settings using CasC`() {
        withSettings<SlackSettings> {
            settingsRepository.deleteAll(SlackSettings::class.java)
            casc("""
                ontrack:
                    config:
                        settings:
                            slack:
                                enabled: true
                                token: some-token
            """.trimIndent())
            val settings = settingsService.getCachedSettings(SlackSettings::class.java)
            assertTrue(settings.enabled)
            assertEquals("some-token", settings.token)
        }
    }

}
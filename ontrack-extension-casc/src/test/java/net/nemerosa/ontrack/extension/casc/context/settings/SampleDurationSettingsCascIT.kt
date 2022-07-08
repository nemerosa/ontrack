package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals

class SampleDurationSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var settingsRepository: SettingsRepository

    @Test
    fun `Casc duration`() {
        withSettings<SampleDurationSettings> {
            settingsRepository.deleteAll(SampleDurationSettings::class.java)
            casc(
                """
                    ontrack:
                        config:
                            settings:
                                sample-duration:
                                    duration: 1209600
                """.trimIndent()
            )
            val settings = settingsService.getCachedSettings(SampleDurationSettings::class.java)
            assertEquals(Duration.ofDays(14), settings.duration)
        }
    }

}
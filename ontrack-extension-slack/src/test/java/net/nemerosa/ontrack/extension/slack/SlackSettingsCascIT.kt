package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackSettingsCascIT : AbstractCascTestSupport() {

    @Test
    fun `Slack settings using CasC`() {
        withCleanSettings<SlackSettings> {
            casc(
                """
                ontrack:
                    config:
                        settings:
                            slack:
                                enabled: true
                                token: some-token
                                emoji: ":ontrack:"
            """.trimIndent()
            )
            val settings = settingsService.getCachedSettings(SlackSettings::class.java)
            assertTrue(settings.enabled)
            assertEquals("some-token", settings.token)
            assertEquals(":ontrack:", settings.emoji)
        }
    }

}
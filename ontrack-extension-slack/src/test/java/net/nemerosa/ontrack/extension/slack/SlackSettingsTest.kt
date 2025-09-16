package net.nemerosa.ontrack.extension.slack

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SlackSettingsTest {

    @Test
    fun obfuscation() {
        val settings = SlackSettings(enabled = true, token = "secret")
        val obfuscated = settings.obfuscate()
        assertEquals("", obfuscated.token)
    }

}
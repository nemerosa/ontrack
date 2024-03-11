package net.nemerosa.ontrack.extension.jira

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JIRAConfigurationTest {

    @Test
    fun obfuscate() {
        var config = JIRAConfiguration("Test", "https://host", "user", "secret", emptyList(), emptyList())
        assertEquals("secret", config.password)
        config = config.obfuscate()
        assertEquals("", config.password)
    }
}

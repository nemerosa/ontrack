package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
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

    @Test
    fun `Minimal JSON`() {
        assertEquals(
            JIRAConfiguration(
                name = "JIRA",
                url = "https://jira",
                user = "some-user",
                password = "some-password",
                include = emptyList(),
                exclude = emptyList(),
            ),
            mapOf(
                "name" to "JIRA",
                "url" to "https://jira",
                "user" to "some-user",
                "password" to "some-password",
            ).asJson().parse<JIRAConfiguration>()
        )
    }
}

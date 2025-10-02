package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.extension.jira.JIRAFixtures.jiraConfiguration
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JIRAConfigurationTest {

    @Test
    fun obfuscate() {
        var config = JIRAConfiguration(
            name = "Test",
            url = "https://host",
            user = "user",
            password = "secret",
            include = emptyList(),
            exclude = emptyList()
        )
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

    @Test
    fun `Validation of issue keys`() {
        val config = jiraConfiguration()
        assertTrue(config.isValidIssueKey("TEST-12"))
        assertTrue(config.isValidIssueKey("PRJ-12"))
        assertTrue(config.isValidIssueKey("PRJ-13"))
        assertTrue(config.isValidIssueKey("PRJX-13"))
        assertTrue(config.isValidIssueKey("PRJ11-12"))
        assertFalse(config.isValidIssueKey("PRJ"))
        assertFalse(config.isValidIssueKey("13"))
        assertFalse(config.isValidIssueKey("13-PRJ"))
        assertFalse(config.isValidIssueKey("1PRJ-12"))
    }

    @Test
    fun `Validate key with exclusion list`() {
        val config = jiraConfiguration(
            exclude = listOf("TEST")
        )
        assertFalse(config.isValidIssueKey("TEST-12"))
        assertTrue(config.isValidIssueKey("PRJ-12"))
        assertTrue(config.isValidIssueKey("PRJ-13"))
        assertTrue(config.isValidIssueKey("PRJX-13"))
        assertTrue(config.isValidIssueKey("PRJ11-12"))
        assertFalse(config.isValidIssueKey("PRJ"))
        assertFalse(config.isValidIssueKey("13"))
        assertFalse(config.isValidIssueKey("13-PRJ"))
        assertFalse(config.isValidIssueKey("1PRJ-12"))
    }

    @Test
    fun `Validate key with inclusion list`() {
        val config = jiraConfiguration(
            include = listOf("PRJ")
        )
        assertFalse(config.isValidIssueKey("TEST-12"))
        assertTrue(config.isValidIssueKey("PRJ-12"))
        assertTrue(config.isValidIssueKey("PRJ-13"))
        assertFalse(config.isValidIssueKey("PRJX-13"))
        assertFalse(config.isValidIssueKey("PRJ11-12"))
        assertFalse(config.isValidIssueKey("PRJ"))
        assertFalse(config.isValidIssueKey("13"))
        assertFalse(config.isValidIssueKey("13-PRJ"))
        assertFalse(config.isValidIssueKey("1PRJ-12"))
    }

    @Test
    fun `Validate key with inclusion regex and exclusion list`() {
        val config = jiraConfiguration(
            include = listOf("PRJ.*"),
            exclude = listOf("PRJX")
        )
        assertFalse(config.isValidIssueKey("TEST-12"))
        assertTrue(config.isValidIssueKey("PRJ-12"))
        assertTrue(config.isValidIssueKey("PRJ-13"))
        assertFalse(config.isValidIssueKey("PRJX-13"))
        assertTrue(config.isValidIssueKey("PRJ11-12"))
        assertFalse(config.isValidIssueKey("PRJ"))
        assertFalse(config.isValidIssueKey("13"))
        assertFalse(config.isValidIssueKey("13-PRJ"))
        assertFalse(config.isValidIssueKey("1PRJ-12"))
    }

    @Test
    fun `Validate key with inclusion and exclusion list`() {
        val config = jiraConfiguration(
            include = listOf("PRJ.*"),
            exclude = listOf("PRJX")
        )
        assertFalse(config.isValidIssueKey("TEST-12"))
        assertTrue(config.isValidIssueKey("PRJ-12"))
        assertTrue(config.isValidIssueKey("PRJ-13"))
        assertFalse(config.isValidIssueKey("PRJX-13"))
        assertTrue(config.isValidIssueKey("PRJ11-12"))
        assertFalse(config.isValidIssueKey("PRJ"))
        assertFalse(config.isValidIssueKey("13"))
        assertFalse(config.isValidIssueKey("13-PRJ"))
        assertFalse(config.isValidIssueKey("1PRJ-12"))
    }

    @Test
    fun `Backward compatibility with no API URL`() {
        val config = mapOf(
            "name" to "JIRA",
            "url" to "https://jira",
            "user" to "some-user",
            "password" to "some-password",
        ).asJson().parse<JIRAConfiguration>()
        assertEquals(null, config.apiUrl)
    }
}

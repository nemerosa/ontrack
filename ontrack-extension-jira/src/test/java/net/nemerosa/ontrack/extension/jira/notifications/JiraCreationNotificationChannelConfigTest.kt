package net.nemerosa.ontrack.extension.jira.notifications

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JiraCreationNotificationChannelConfigTest {

    @Test
    fun `Minimal JSON`() {
        val json = mapOf(
            "configName" to "JIRA",
            "projectName" to "ONTRACK",
            "issueType" to "Test",
            "titleTemplate" to "Summary test",
        ).asJson()
        assertEquals(
            JiraCreationNotificationChannelConfig(
                configName = "JIRA",
                projectName = "ONTRACK",
                issueType = "Test",
                labels = emptyList(),
                fixVersion = null,
                assignee = null,
                titleTemplate = "Summary test",
                customFields = emptyMap(),
            ),
            json.parse()
        )
    }

    @Test
    fun `JSON with custom fields`() {
        val json = mapOf(
            "configName" to "JIRA",
            "projectName" to "ONTRACK",
            "issueType" to "Test",
            "titleTemplate" to "Summary test",
            "customFields" to mapOf(
                "duedate" to "2024-04-16",
                "customfield_11000" to "Some direct value",
                "customfield_12000" to mapOf(
                    "value" to "Some map value"
                ),
            )
        ).asJson()
        assertEquals(
            JiraCreationNotificationChannelConfig(
                configName = "JIRA",
                projectName = "ONTRACK",
                issueType = "Test",
                labels = emptyList(),
                fixVersion = null,
                assignee = null,
                titleTemplate = "Summary test",
                customFields = mapOf(
                    "duedate" to TextNode("2024-04-16"),
                    "customfield_11000" to TextNode("Some direct value"),
                    "customfield_12000" to mapOf(
                        "value" to "Some map value"
                    ).asJson(),
                )
            ),
            json.parse()
        )
    }

}
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
                customFields = emptyList(),
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
            "customFields" to listOf(
                mapOf(
                    "name" to "duedate",
                    "value" to "2024-04-16",
                ),
                mapOf(
                    "name" to "customfield_11000",
                    "value" to "Some direct value",
                ),
                mapOf(
                    "name" to "customfield_12000",
                    "value" to mapOf(
                        "value" to "Some map value"
                    ),
                ),
            ),
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
                customFields = listOf(
                    JiraCustomField("duedate", TextNode("2024-04-16")),
                    JiraCustomField("customfield_11000", TextNode("Some direct value")),
                    JiraCustomField(
                        "customfield_12000", mapOf(
                            "value" to "Some map value"
                        ).asJson()
                    ),
                ),
            ),
            json.parse()
        )
    }

}
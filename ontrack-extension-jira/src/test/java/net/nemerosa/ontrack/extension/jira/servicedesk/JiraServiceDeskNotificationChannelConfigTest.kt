package net.nemerosa.ontrack.extension.jira.servicedesk

import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JiraServiceDeskNotificationChannelConfigTest {

    @Test
    fun `Minimal JSON`() {
        val json = mapOf(
            "configName" to "JIRA",
            "serviceDeskId" to 101,
            "requestTypeId" to 1001,
            "fields" to listOf(
                mapOf(
                    "name" to "some_field",
                    "value" to mapOf("name" to "some_name")
                )
            ),
            "searchTerm" to "Search terms"
        ).asJson()
        assertEquals(
            JiraServiceDeskNotificationChannelConfig(
                configName = "JIRA",
                useExisting = false,
                serviceDeskId = 101,
                requestTypeId = 1001,
                fields = listOf(
                    JiraCustomField(
                        name = "some_field",
                        value = mapOf("name" to "some_name").asJson()
                    )
                ),
                searchTerm = "Search terms"
            ),
            json.parse()
        )
    }

}
package net.nemerosa.ontrack.extension.workflows.mgt

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class WorkflowSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var workflowSettingsCasc: WorkflowSettingsCasc

    @Test
    fun `CasC schema type`() {
        val type = workflowSettingsCasc.jsonType
        assertEquals(
            """
                {
                  "title": "WorkflowSettings",
                  "description": null,
                  "properties": {
                    "retentionDuration": {
                      "description": "Number of milliseconds before workflow instances are removed",
                      "type": "integer"
                    }
                  },
                  "required": [],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

}
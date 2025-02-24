package net.nemerosa.ontrack.extension.workflows.mgt

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class WorkflowSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var workflowSettingsCasc: WorkflowSettingsCasc

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = workflowSettingsCasc.jsonType(jsonTypeBuilder)
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
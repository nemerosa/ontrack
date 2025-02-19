package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PredefinedValidationStampsAdminContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var predefinedValidationStampsAdminContext: PredefinedValidationStampsAdminContext

    @Test
    fun `CasC schema type`() {
        val type = predefinedValidationStampsAdminContext.jsonType
        assertEquals(
            """
                {
                  "title": "PredefinedValidationStampsAdminContextType",
                  "description": null,
                  "properties": {
                    "list": {
                      "items": {
                        "title": "PredefinedValidationStampsAdminContextTypeItem",
                        "description": "List of validation stamps to predefine",
                        "properties": {
                          "description": {
                            "description": "Description of the validation stamp",
                            "type": "string"
                          },
                          "image": {
                            "description": "Path to the validation stamp image",
                            "type": "string"
                          },
                          "name": {
                            "description": "Name of the validation stamp",
                            "type": "string"
                          }
                        },
                        "required": [
                          "name"
                        ],
                        "additionalProperties": false,
                        "type": "object"
                      },
                      "description": "List of validation stamps to predefine",
                      "type": "array"
                    },
                    "replace": {
                      "description": "Is the list authoritative?",
                      "type": "boolean"
                    }
                  },
                  "required": [
                    "list"
                  ],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

}
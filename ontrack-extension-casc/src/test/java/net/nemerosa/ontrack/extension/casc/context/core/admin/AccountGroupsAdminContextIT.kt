package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.security.AccountGroupInput
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AccountGroupsAdminContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var accountGroupsAdminContext: AccountGroupsAdminContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = accountGroupsAdminContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "items": {
                    "title": "AccountGroupInput",
                    "description": null,
                    "properties": {
                      "description": {
                        "description": "Description of the group",
                        "type": "string"
                      },
                      "name": {
                        "description": "Name of the group",
                        "type": "string"
                      }
                    },
                    "required": [
                      "name"
                    ],
                    "additionalProperties": false,
                    "type": "object"
                  },
                  "description": "List of account groups",
                  "type": "array"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Creating new groups`() {
        asAdmin {
            val name = uid("g")
            casc(
                """
                ontrack:
                    admin:
                        groups:
                            - name: $name
                              description: Group description
            """.trimIndent()
            )
            // Checks the group has been created
            assertNotNull(accountService.findAccountGroupByName(name), "Group created") {
                assertEquals("Group description", it.description)
            }
        }
    }

    @Test
    fun `Updating groups`() {
        asAdmin {
            val name = uid("g")
            val group = accountService.createGroup(AccountGroupInput(name, "Initial description"))
            // Modifying the group
            casc(
                """
                ontrack:
                    admin:
                        groups:
                            - name: $name
                              description: Group description
            """.trimIndent()
            )
            // Checks the group has been created
            assertNotNull(accountService.getAccountGroup(group.id), "Group updated") {
                assertEquals("Group description", it.description)
            }
        }
    }

    @Test
    fun `Existing groups are left as-is`() {
        asAdmin {
            val name = uid("g")
            val group = accountService.createGroup(AccountGroupInput(name, "Initial description"))
            // Modifying/creating other groups
            val otherName = uid("g")
            casc(
                """
                ontrack:
                    admin:
                        groups:
                            - name: $otherName
                              description: Group description
            """.trimIndent()
            )
            // Checks the existing group is still there
            assertNotNull(accountService.getAccountGroup(group.id), "Group still there")
        }
    }

}
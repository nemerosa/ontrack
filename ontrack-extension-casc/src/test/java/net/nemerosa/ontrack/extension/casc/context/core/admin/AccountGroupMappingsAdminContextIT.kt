package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.security.AccountGroupMappingService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class AccountGroupMappingsAdminContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var accountGroupMappingService: AccountGroupMappingService

    @Autowired
    private lateinit var accountGroupMappingsAdminContext: AccountGroupMappingsAdminContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = accountGroupMappingsAdminContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "items": {
                    "title": "CascMapping",
                    "description": null,
                    "properties": {
                      "group": {
                        "description": "Name of the group in Ontrack",
                        "type": "string"
                      },
                      "provider": {
                        "description": "ID of the authentication provider: oidc, ldap, ...",
                        "type": "string"
                      },
                      "providerGroup": {
                        "description": "Name of the group in the provider",
                        "type": "string"
                      },
                      "providerKey": {
                        "description": "Identifier of the exact provider source (name of the OIDC provider, leave blank for LDAP)",
                        "type": "string"
                      }
                    },
                    "required": [
                      "group",
                      "provider",
                      "providerGroup",
                      "providerKey"
                    ],
                    "additionalProperties": false,
                    "type": "object"
                  },
                  "description": "List of group mappings",
                  "type": "array"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Creation of new mappings`() {
        asAdmin {
            val name = uid("g")
            casc("""
                ontrack:
                    admin:
                        groups:
                            - name: $name
                              description: My group
                        group-mappings:
                            - provider: test
                              providerKey: ""
                              providerGroup: "test-group"
                              group: $name
            """.trimIndent())
            val mapping = accountService.findAccountGroupByName(name)?.let {
                accountGroupMappingService.getMappingsForGroup(it)
            }?.firstOrNull()
            assertNotNull(mapping, "Mapping created") {
                assertEquals("test", it.authenticationSource.provider)
                assertEquals("", it.authenticationSource.key)
                assertEquals("test-group", it.name)
                assertEquals(name, it.group.name)
            }
        }
    }

    @Test
    fun `Updating existing mappings`() {
        asAdmin {
            val name = uid("g")
            repeat(2) {
                casc("""
                    ontrack:
                        admin:
                            groups:
                                - name: $name
                                  description: My group
                            group-mappings:
                                - provider: test
                                  providerKey: ""
                                  providerGroup: "test-group"
                                  group: $name
                """.trimIndent())
            }
            val mapping = accountService.findAccountGroupByName(name)?.let {
                accountGroupMappingService.getMappingsForGroup(it)
            }?.firstOrNull()
            assertNotNull(mapping, "Mapping created") {
                assertEquals("test", it.authenticationSource.provider)
                assertEquals("", it.authenticationSource.key)
                assertEquals("test-group", it.name)
                assertEquals(name, it.group.name)
            }
        }
    }

    @Test
    fun `Mapping fails if group is not declared`() {
        asAdmin {
            val name = uid("g")
            assertFailsWith<IllegalStateException> {
                casc("""
                    ontrack:
                        admin:
                            group-mappings:
                                - provider: test
                                  providerKey: ""
                                  providerGroup: "test-group"
                                  group: $name
                """.trimIndent())
            }
        }
    }

    @Test
    fun `Creation of mappings before the groups works OK before of ordering`() {
        asAdmin {
            val name = uid("g")
            casc("""
                    ontrack:
                        admin:
                            group-mappings:
                                - provider: test
                                  providerKey: ""
                                  providerGroup: "test-group"
                                  group: $name
                            groups:
                                - name: $name
                                  description: My group
                """.trimIndent())
            val mapping = accountService.findAccountGroupByName(name)?.let {
                accountGroupMappingService.getMappingsForGroup(it)
            }?.firstOrNull()
            assertNotNull(mapping, "Mapping created") {
                assertEquals("test", it.authenticationSource.provider)
                assertEquals("", it.authenticationSource.key)
                assertEquals("test-group", it.name)
                assertEquals(name, it.group.name)
            }
        }
    }

}
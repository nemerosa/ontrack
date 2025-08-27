package net.nemerosa.ontrack.extension.oidc.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OIDCConfigurationAsCodeIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var oidcSettingsService: OIDCSettingsService

    @Autowired
    private lateinit var oidcCascContext: OIDCCascContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `OIDC CasC schema type`() {
        val type = oidcCascContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "items": {
                    "title": "OntrackOIDCProvider",
                    "description": null,
                    "properties": {
                      "clientId": {
                        "description": "OIDC client ID",
                        "type": "string"
                      },
                      "clientSecret": {
                        "description": "OIDC client secret",
                        "type": "string"
                      },
                      "description": {
                        "description": "Tooltip for this provider",
                        "type": "string"
                      },
                      "disabled": {
                        "description": "If true, this provider is disabled and won't be active",
                        "type": "boolean"
                      },
                      "forceHttps": {
                        "description": "Check to force the protocol to HTTPS for the Redirect URI",
                        "type": "boolean"
                      },
                      "groupClaim": {
                        "description":"Name of the access token claim that contains the list of groups. It defaults to `groups`.",
                        "type":"string"
                      },
                      "groupFilter": {
                        "description": "Regular expression used to filter groups associated with the OIDC user",
                        "type": "string"
                      },
                      "id": {
                        "description": "Unique ID for this provider",
                        "type": "string"
                      },
                      "issuerId": {
                        "description": "OIDC issueId URL",
                        "type": "string"
                      },
                      "name": {
                        "description": "Display name for this provider",
                        "type": "string"
                      }
                    },
                    "required": [
                      "clientId",
                      "clientSecret",
                      "id",
                      "issuerId",
                      "name"
                    ],
                    "additionalProperties": false,
                    "type": "object"
                  },
                  "description": "List of OIDC providers",
                  "type": "array"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `OIDC provider registration`() {
        val id = uid("O")
        casc(
            """
            ontrack:
                config:
                    oidc:
                        - id: $id
                          name: My Keycloak
                          description: My Keycloak instance
                          issuerId: some-issuer-id
                          clientId: some-client-id
                          groupFilter: ontrack-.*
        """.trimIndent()
        )
        // Checks the OIDC provider has been registered
        asAdmin {
            assertNotNull(oidcSettingsService.getProviderById(id)) {
                assertEquals(id, it.id)
                assertEquals("My Keycloak", it.name)
                assertEquals("My Keycloak instance", it.description)
                assertEquals("some-issuer-id", it.issuerId)
                assertEquals("some-client-id", it.clientId)
                assertEquals("", it.clientSecret)
                assertEquals("ontrack-.*", it.groupFilter)
            }
        }
    }

    @Test
    fun `OIDC provider registration without a group filter`() {
        val id = uid("O")
        casc(
            """
            ontrack:
                config:
                    oidc:
                        - id: $id
                          name: My Keycloak
                          description: My Keycloak instance
                          issuerId: some-issuer-id
                          clientId: some-client-id
        """.trimIndent()
        )
        // Checks the OIDC provider has been registered
        asAdmin {
            assertNotNull(oidcSettingsService.getProviderById(id)) {
                assertEquals(id, it.id)
                assertEquals("My Keycloak", it.name)
                assertEquals("My Keycloak instance", it.description)
                assertEquals("some-issuer-id", it.issuerId)
                assertEquals("some-client-id", it.clientId)
                assertEquals("", it.clientSecret)
                assertEquals(null, it.groupFilter)
            }
        }
    }

    @Test
    fun `OIDC provider registration is idempotent`() {
        val id = uid("O")
        val yaml = """
            ontrack:
                config:
                    oidc:
                        - id: $id
                          name: My Keycloak
                          description: My Keycloak instance
                          issuerId: some-issuer-id
                          clientId: some-client-id
        """.trimIndent()
        // Once
        casc(yaml)
        // Checks the OIDC provider has been registered
        asAdmin {
            assertNotNull(oidcSettingsService.getProviderById(id)) {
                assertEquals(id, it.id)
                assertEquals("My Keycloak", it.name)
                assertEquals("My Keycloak instance", it.description)
                assertEquals("some-issuer-id", it.issuerId)
                assertEquals("some-client-id", it.clientId)
                assertEquals("", it.clientSecret)
                assertEquals(null, it.groupFilter)
            }
        }
        // Twice
        casc(yaml)
        // Checks the OIDC provider is still registered with the same values
        asAdmin {
            assertNotNull(oidcSettingsService.getProviderById(id)) {
                assertEquals(id, it.id)
                assertEquals("My Keycloak", it.name)
                assertEquals("My Keycloak instance", it.description)
                assertEquals("some-issuer-id", it.issuerId)
                assertEquals("some-client-id", it.clientId)
                assertEquals("", it.clientSecret)
                assertEquals(null, it.groupFilter)
            }
        }
    }

    @Test
    fun `OIDC provider registration with update`() {
        val id = uid("O")
        // Once
        casc(
            """
            ontrack:
                config:
                    oidc:
                        - id: $id
                          name: My Keycloak
                          description: My Keycloak instance
                          issuerId: some-issuer-id
                          clientId: some-client-id
        """.trimIndent()
        )
        // Checks the OIDC provider has been registered
        asAdmin {
            assertNotNull(oidcSettingsService.getProviderById(id)) {
                assertEquals(id, it.id)
                assertEquals("My Keycloak", it.name)
                assertEquals("My Keycloak instance", it.description)
                assertEquals("some-issuer-id", it.issuerId)
                assertEquals("some-client-id", it.clientId)
                assertEquals("", it.clientSecret)
                assertEquals(null, it.groupFilter)
            }
        }
        // With some changes
        casc(
            """
            ontrack:
                config:
                    oidc:
                        - id: $id
                          name: My Keycloak
                          description: My Keycloak instance
                          issuerId: some-issuer-id
                          clientId: another-client-id
        """.trimIndent()
        )
        // Checks the OIDC provider is still registered with the new values
        asAdmin {
            assertNotNull(oidcSettingsService.getProviderById(id)) {
                assertEquals(id, it.id)
                assertEquals("My Keycloak", it.name)
                assertEquals("My Keycloak instance", it.description)
                assertEquals("some-issuer-id", it.issuerId)
                assertEquals("another-client-id", it.clientId)
                assertEquals("", it.clientSecret)
                assertEquals(null, it.groupFilter)
            }
        }
    }

    @Test
    fun `Registering a list of providers`() {
        val id = uid("O")
        // Once
        casc(
            """
            ontrack:
                config:
                    oidc:
                        - id: ${id}1
                          name: My Keycloak 1
                          description: My Keycloak instance 1
                          issuerId: some-issuer-id-1
                          clientId: some-client-id-1
                        - id: ${id}2
                          name: My Keycloak 2
                          description: My Keycloak instance 2
                          issuerId: some-issuer-id-2
                          clientId: some-client-id-2
        """.trimIndent()
        )
        // Checks the OIDC providers have been registered
        asAdmin {
            (1..2).forEach { no ->
                assertNotNull(oidcSettingsService.getProviderById("$id$no")) {
                    assertEquals("$id$no", it.id)
                    assertEquals("My Keycloak $no", it.name)
                    assertEquals("My Keycloak instance $no", it.description)
                    assertEquals("some-issuer-id-$no", it.issuerId)
                    assertEquals("some-client-id-$no", it.clientId)
                    assertEquals("", it.clientSecret)
                    assertEquals(null, it.groupFilter)
                }
            }
        }
    }

    @Test
    fun `Removing from a list of providers removes the provider`() {
        val id = uid("O")
        // Once with two providers
        casc(
            """
            ontrack:
                config:
                    oidc:
                        - id: ${id}1
                          name: My Keycloak 1
                          description: My Keycloak instance 1
                          issuerId: some-issuer-id-1
                          clientId: some-client-id-1
                        - id: ${id}2
                          name: My Keycloak 2
                          description: My Keycloak instance 2
                          issuerId: some-issuer-id-2
                          clientId: some-client-id-2
        """.trimIndent()
        )
        // Checks the OIDC providers have been registered
        asAdmin {
            assertEquals(2, oidcSettingsService.providers.size)
            (1..2).forEach { no ->
                assertNotNull(oidcSettingsService.getProviderById("$id$no")) {
                    assertEquals("$id$no", it.id)
                    assertEquals("My Keycloak $no", it.name)
                    assertEquals("My Keycloak instance $no", it.description)
                    assertEquals("some-issuer-id-$no", it.issuerId)
                    assertEquals("some-client-id-$no", it.clientId)
                    assertEquals("", it.clientSecret)
                    assertEquals(null, it.groupFilter)
                }
            }
        }
        // Removes a provider from the list...
        casc(
            """
            ontrack:
                config:
                    oidc:
                        - id: ${id}2
                          name: My Keycloak 2
                          description: My Keycloak instance 2
                          issuerId: some-issuer-id-2
                          clientId: some-client-id-2
        """.trimIndent()
        )
        // Checks the OIDC provider has been removed
        asAdmin {
            assertEquals(1, oidcSettingsService.providers.size)
            assertNotNull(oidcSettingsService.getProviderById("${id}2")) {
                assertEquals("${id}2", it.id)
                assertEquals("My Keycloak 2", it.name)
                assertEquals("My Keycloak instance 2", it.description)
                assertEquals("some-issuer-id-2", it.issuerId)
                assertEquals("some-client-id-2", it.clientId)
                assertEquals("", it.clientSecret)
                assertEquals(null, it.groupFilter)
            }
        }
    }

}
package net.nemerosa.ontrack.extension.oidc.casc

import net.nemerosa.ontrack.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OIDCConfigurationAsCodeIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var oidcSettingsService: OIDCSettingsService

    @Test
    fun `OIDC provider registration`() {
        val id = uid("O")
        casc("""
            ontrack:
                config:
                    security:
                        oidc:
                            - id: $id
                              name: My Keycloak
                              description: My Keycloak instance
                              issuer-id: some-issuer-id
                              client-id: some-client-id
                              group-filter: ontrack-.*
        """.trimIndent())
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
        casc("""
            ontrack:
                config:
                    security:
                        oidc:
                            - id: $id
                              name: My Keycloak
                              description: My Keycloak instance
                              issuer-id: some-issuer-id
                              client-id: some-client-id
        """.trimIndent())
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
                    security:
                        oidc:
                            - id: $id
                              name: My Keycloak
                              description: My Keycloak instance
                              issuer-id: some-issuer-id
                              client-id: some-client-id
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
        casc("""
            ontrack:
                config:
                    security:
                        oidc:
                            - id: $id
                              name: My Keycloak
                              description: My Keycloak instance
                              issuer-id: some-issuer-id
                              client-id: some-client-id
        """.trimIndent())
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
        casc("""
            ontrack:
                config:
                    security:
                        oidc:
                            - id: $id
                              name: My Keycloak
                              description: My Keycloak instance
                              issuer-id: some-issuer-id
                              client-id: another-client-id
        """.trimIndent())
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
        casc("""
            ontrack:
                config:
                    security:
                        oidc:
                            - id: ${id}1
                              name: My Keycloak 1
                              description: My Keycloak instance 1
                              issuer-id: some-issuer-id-1
                              client-id: some-client-id-1
                            - id: ${id}2
                              name: My Keycloak 2
                              description: My Keycloak instance 2
                              issuer-id: some-issuer-id-2
                              client-id: some-client-id-2
        """.trimIndent())
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
        casc("""
            ontrack:
                config:
                    security:
                        oidc:
                            - id: ${id}1
                              name: My Keycloak 1
                              description: My Keycloak instance 1
                              issuer-id: some-issuer-id-1
                              client-id: some-client-id-1
                            - id: ${id}2
                              name: My Keycloak 2
                              description: My Keycloak instance 2
                              issuer-id: some-issuer-id-2
                              client-id: some-client-id-2
        """.trimIndent())
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
        casc("""
            ontrack:
                config:
                    security:
                        oidc:
                            - id: ${id}2
                              name: My Keycloak 2
                              description: My Keycloak instance 2
                              issuer-id: some-issuer-id-2
                              client-id: some-client-id-2
        """.trimIndent())
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
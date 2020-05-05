package net.nemerosa.ontrack.extension.oidc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.extension.oidc.settings.OntrackOIDCProvider
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import kotlin.test.assertEquals
import kotlin.test.assertSame

class OntrackClientRegistrationRepositoryIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var repository: OntrackClientRegistrationRepository

    @Autowired
    private lateinit var oidcSettingsService: OIDCSettingsService

    private val mapper = ObjectMapper()
    private lateinit var server: MockWebServer
    private lateinit var response: MutableMap<String, Any>
    private lateinit var issuer: String

    @Before
    fun before() {
        server = MockWebServer()
        server.start()
        response = mapper.readValue(DEFAULT_RESPONSE, object : TypeReference<MutableMap<String, Any>>() {})

        issuer = server.url("").toString()
        response["issuer"] = issuer

        val responseBody = mapper.writeValueAsString(response)

        val dispatcher: Dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                    when (request.path) {
                        "/.well-known/openid-configuration/" ->
                            buildSuccessMockResponse(responseBody)
                        else -> MockResponse().setResponseCode(404)
                    }
        }
        server.dispatcher = dispatcher
    }

    private fun buildSuccessMockResponse(body: String): MockResponse =
            MockResponse().setResponseCode(200)
                    .setBody(body)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

    @After
    fun cleanup() {
        server.shutdown()
        asAdmin {
            oidcSettingsService.providers.forEach {
                oidcSettingsService.deleteProvider(it.id)
            }
        }
    }

    @Test
    fun `List of registrations cached`() {
        val ids = providers(
                provider(),
                provider()
        )
        // Gets the registrations
        val registrations = repository.registrations
        assertEquals(ids.toSet(), registrations.values.map { it.registrationId }.toSet())
        // Gets them back
        val registrationAgain = repository.registrations
        assertSame(registrations, registrationAgain)
    }

    @Test
    fun `List of registrations updated automatically`() {
        val ids = providers(
                provider(),
                provider()
        )
        // Gets the registrations
        var registrations = repository.registrations
        assertEquals(ids.toSet(), registrations.values.map { it.registrationId }.toSet())
        // Adds a provider
        val additionalIds = providers(
                provider()
        )
        registrations = repository.registrations
        assertEquals((ids + additionalIds).toSet(), registrations.values.map { it.registrationId }.toSet())
    }

    private fun providers(vararg providers: OntrackOIDCProvider): List<String> {
        asAdmin {
            providers.forEach { provider ->
                oidcSettingsService.createProvider(provider)
            }
        }
        return providers.toList().map { it.id }
    }

    private fun provider(
            id: String = uid("P")
    ) = OntrackOIDCProvider(
            id = id,
            name = "$id name",
            description = "",
            issuerId = issuer,
            clientId = "xxx",
            clientSecret = "",
            groupFilter = null
    )

    companion object {

        /**
         * Contains all optional parameters that are found in ClientRegistration
         */
        private const val DEFAULT_RESPONSE = """{
            "authorization_endpoint": "https://example.com/o/oauth2/v2/auth", 
            "claims_supported": [
                "aud", 
                "email", 
                "email_verified", 
                "exp", 
                "family_name", 
                "given_name", 
                "iat", 
                "iss", 
                "locale", 
                "name", 
                "picture", 
                "sub"
            ], 
            "code_challenge_methods_supported": [
                "plain", 
                "S256"
            ], 
            "id_token_signing_alg_values_supported": [
                "RS256"
            ], 
            "issuer": "https://example.com", 
            "jwks_uri": "https://example.com/oauth2/v3/certs", 
            "response_types_supported": [
                "code", 
                "token", 
                "id_token", 
                "code token", 
                "code id_token", 
                "token id_token", 
                "code token id_token", 
                "none"
            ], 
            "revocation_endpoint": "https://example.com/o/oauth2/revoke", 
            "scopes_supported": [
                "openid", 
                "email", 
                "profile"
            ], 
            "subject_types_supported": [
                "public"
            ], 
            "grant_types_supported" : ["authorization_code"], 
            "token_endpoint": "https://example.com/oauth2/v4/token", 
            "token_endpoint_auth_methods_supported": [
                "client_secret_post", 
                "client_secret_basic", 
                "none"
            ], 
            "userinfo_endpoint": "https://example.com/oauth2/v3/userinfo"
        }"""
    }

}
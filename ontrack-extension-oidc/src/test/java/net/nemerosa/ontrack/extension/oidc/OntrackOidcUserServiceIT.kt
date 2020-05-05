package net.nemerosa.ontrack.extension.oidc

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.extension.oidc.settings.OntrackOIDCProvider
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.AccountInput
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.ProvidedGroupsService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertIs
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OntrackOidcUserServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var providedGroupsService: ProvidedGroupsService

    @Autowired
    private lateinit var oidcSettingsService: OIDCSettingsService

    private lateinit var userService: OntrackOidcUserService

    private lateinit var clientRegistration: OntrackClientRegistration
    private lateinit var oidcUserInfo: OidcUserInfo
    private lateinit var oidcUser: OidcUser

    @Before
    fun before() {
        userService = OntrackOidcUserService(
                accountService,
                securityService,
                providedGroupsService,
                oidcSettingsService
        )

        clientRegistration = mock()
        whenever(clientRegistration.registrationId).thenReturn("test")
        whenever(clientRegistration.clientName).thenReturn("Test")

        oidcUserInfo = mock()

        oidcUser = mock()
        whenever(oidcUser.userInfo).thenReturn(oidcUserInfo)
    }

    @After
    fun cleanup() {
        asAdmin {
            oidcSettingsService.providers.forEach {
                oidcSettingsService.deleteProvider(it.id)
            }
        }
    }

    @Test
    fun `Email not available`() {
        whenever(oidcUserInfo.email).thenReturn(null)
        assertFailsWith<OidcEmailRequiredException> {
            userService.linkOidcUser(clientRegistration, oidcUser)
        }
    }

    @Test
    fun `Email not filled`() {
        whenever(oidcUserInfo.email).thenReturn("")
        assertFailsWith<OidcEmailRequiredException> {
            userService.linkOidcUser(clientRegistration, oidcUser)
        }
    }

    @Test
    fun `Account to be created with full name as email`() {
        val email = "${uid("n")}@nemerosa.net"
        whenever(oidcUserInfo.email).thenReturn(email)

        registerProvider()

        val user = userService.linkOidcUser(clientRegistration, oidcUser)

        assertIs<OntrackOidcUser>(user) { ontrackUser ->
            val accountId = ontrackUser.id()
            asAdmin {
                val account = accountService.getAccount(ID.of(accountId))
                assertEquals(email, account.name)
                assertEquals(email, account.email)
                assertEquals(email, account.fullName)
                assertEquals("oidc", account.authenticationSource.provider)
                assertEquals("test", account.authenticationSource.key)
            }
        }
    }

    @Test
    fun `Account to be created with specified full name`() {
        val email = "${uid("n")}@nemerosa.net"
        val fullName = uid("N")
        whenever(oidcUserInfo.email).thenReturn(email)
        whenever(oidcUser.fullName).thenReturn(fullName)

        registerProvider()

        val user = userService.linkOidcUser(clientRegistration, oidcUser)

        assertIs<OntrackOidcUser>(user) { ontrackUser ->
            val accountId = ontrackUser.id()
            asAdmin {
                val account = accountService.getAccount(ID.of(accountId))
                assertEquals(email, account.name)
                assertEquals(email, account.email)
                assertEquals(fullName, account.fullName)
                assertEquals("oidc", account.authenticationSource.provider)
                assertEquals("test", account.authenticationSource.key)
            }
        }
    }

    @Test
    fun `No filter on groups`() {
        val email = "${uid("n")}@nemerosa.net"
        val fullName = uid("N")
        whenever(oidcUserInfo.email).thenReturn(email)
        whenever(oidcUser.fullName).thenReturn(fullName)

        whenever(oidcUser.getClaimAsStringList("groups")).thenReturn(
                listOf(
                        "ontrack-admins",
                        "ontrack-users",
                        "other-group"
                )
        )

        registerProvider()

        val user = userService.linkOidcUser(clientRegistration, oidcUser)

        assertIs<OntrackOidcUser>(user) { ontrackUser ->
            val accountId = ontrackUser.id()
            asAdmin {
                val authSource = OidcAuthenticationSourceProvider.asSource(clientRegistration)
                val groups = providedGroupsService.getProvidedGroups(accountId, authSource)
                assertEquals(
                        setOf(
                                "ontrack-admins",
                                "ontrack-users",
                                "other-group"
                        ),
                        groups
                )
            }
        }
    }

    @Test
    fun `Filter on groups`() {
        val email = "${uid("n")}@nemerosa.net"
        val fullName = uid("N")
        whenever(oidcUserInfo.email).thenReturn(email)
        whenever(oidcUser.fullName).thenReturn(fullName)

        whenever(oidcUser.getClaimAsStringList("groups")).thenReturn(
                listOf(
                        "ontrack-admins",
                        "ontrack-users",
                        "other-group"
                )
        )

        registerProvider(groupFilter = "ontrack-.*")

        val user = userService.linkOidcUser(clientRegistration, oidcUser)

        assertIs<OntrackOidcUser>(user) { ontrackUser ->
            val accountId = ontrackUser.id()
            asAdmin {
                val authSource = OidcAuthenticationSourceProvider.asSource(clientRegistration)
                val groups = providedGroupsService.getProvidedGroups(accountId, authSource)
                assertEquals(
                        setOf(
                                "ontrack-admins",
                                "ontrack-users"
                        ),
                        groups
                )
            }
        }
    }

    @Test
    fun `With existing account`() {
        val email = "${uid("n")}@nemerosa.net"
        val fullName = uid("N")
        whenever(oidcUserInfo.email).thenReturn(email)
        whenever(oidcUser.fullName).thenReturn(fullName)

        registerProvider()

        val user = userService.linkOidcUser(clientRegistration, oidcUser)

        assertIs<OntrackOidcUser>(user) { ontrackUser ->
            val account = ontrackUser.account
            // Logs a second time
            val secondUser = userService.linkOidcUser(clientRegistration, oidcUser)
            // Checks the account is the same
            assertIs<OntrackOidcUser>(secondUser) {
                assertEquals(account.id, it.account.id, "Using the same account")
            }
        }
    }

    @Test
    fun `With existing account from another provider`() {
        val email = "${uid("n")}@nemerosa.net"
        val fullName = uid("N")
        whenever(oidcUserInfo.email).thenReturn(email)
        whenever(oidcUser.fullName).thenReturn(fullName)

        registerProvider()
        registerProvider(id = "other")

        // Creates an account using this provider
        asAdmin {
            accountService.create(
                    AccountInput(
                            name = email,
                            fullName = fullName,
                            email = email,
                            password = null,
                            groups = emptySet()
                    ),
                    AuthenticationSource(
                            provider = "oidc",
                            key = "other",
                            name = "Other"
                    )
            )
        }

        assertFailsWith<OidcNonOidcExistingUserException> {
            userService.linkOidcUser(clientRegistration, oidcUser)
        }
    }

    private fun registerProvider(
            id: String = "test",
            name: String = "Test",
            groupFilter: String? = null
    ) {
        asAdmin {
            val provider = oidcSettingsService.getProviderById(id)
            if (provider == null) {
                oidcSettingsService.createProvider(
                        OntrackOIDCProvider(
                                id = id,
                                name = name,
                                description = "",
                                issuerId = "",
                                clientId = "",
                                clientSecret = "",
                                groupFilter = groupFilter
                        )
                )
            } else {
                oidcSettingsService.updateProvider(
                        OntrackOIDCProvider(
                                id = id,
                                name = name,
                                description = "",
                                issuerId = "",
                                clientId = "",
                                clientSecret = "",
                                groupFilter = null
                        )
                )
            }
        }
    }
}
package net.nemerosa.ontrack.extension.ldap

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider
import org.springframework.security.ldap.userdetails.LdapUserDetails
import java.util.*
import java.util.function.Supplier
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LDAPAuthenticationProviderTest {

    private lateinit var provider: LDAPAuthenticationProvider
    private lateinit var ldapProviderFactory: LDAPProviderFactory
    private lateinit var ldapAuthenticationProvider: LdapAuthenticationProvider
    private lateinit var ldapAuthenticationSourceProvider: LDAPAuthenticationSourceProvider
    private lateinit var securityService: SecurityService
    private lateinit var accountService: AccountService

    @Before
    fun before() {
        accountService = mock()
        ldapProviderFactory = mock()
        ldapAuthenticationSourceProvider = LDAPAuthenticationSourceProvider()
        securityService = mock()
        ldapAuthenticationProvider = mock()
        val applicationLogService: ApplicationLogService = mock()
        provider = LDAPAuthenticationProvider(
                accountService,
                ldapProviderFactory,
                ldapAuthenticationSourceProvider,
                securityService,
                applicationLogService
        )

        whenever(securityService.asAdmin(any<Supplier<Any>>())).then { invocation ->
            val supplier = invocation.getArgument<Supplier<Any>>(0)
            supplier.get()
        }

    }

    @Test
    fun `Cannot authenticate when LDAP is disabled`() {
        val user = provider.findUser("test", UsernamePasswordAuthenticationToken("test", "xxx"))
        assertNull(user)
    }

    @Test
    fun `LDAP authentication failure when no authentication returned`() {
        whenever(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)
        val user = provider.findUser("test", UsernamePasswordAuthenticationToken("test", "xxx"))
        assertNull(user)
    }

    @Test
    fun `LDAP authentication failure when partial authentication returned`() {
        whenever(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)
        val authentication = mock<UsernamePasswordAuthenticationToken>()
        whenever(authentication.isAuthenticated).thenReturn(false)
        whenever(ldapAuthenticationProvider.authenticate(authentication)).thenReturn(authentication)
        val user = provider.findUser("test", authentication)
        assertNull(user)
    }

    @Test
    fun `LDAP authentication success and existing account`() {
        whenever(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)

        val authentication = mock<UsernamePasswordAuthenticationToken>()
        whenever(authentication.name).thenReturn("test")
        whenever(authentication.isAuthenticated).thenReturn(true)
        whenever(ldapAuthenticationProvider.authenticate(authentication)).thenReturn(authentication)

        val account = Account.of(
                "test",
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                ldapAuthenticationSourceProvider.source
        )
        whenever(accountService.findUserByNameAndSource("test", ldapAuthenticationSourceProvider)).thenReturn(
                Optional.of(
                        account
                )
        )

        val authAccount = provider.findUser("test", authentication)
        assertNotNull(authAccount) {
            assertEquals(account, it.account)
        }
    }

    @Test
    fun `LDAP authentication success, no LDAP detail`() {
        whenever(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)

        val authentication = mock<UsernamePasswordAuthenticationToken>()
        whenever(authentication.name).thenReturn("test")
        whenever(authentication.isAuthenticated).thenReturn(true)
        whenever(authentication.principal).thenReturn("No custom")
        whenever(ldapAuthenticationProvider.authenticate(authentication)).thenReturn(authentication)

        val tempAccount = Account.of(
                "test",
                "test",
                "",
                SecurityRole.USER,
                ldapAuthenticationSourceProvider.source
        )
        whenever(accountService.findUserByNameAndSource("test", ldapAuthenticationSourceProvider)).thenReturn(
                Optional.empty()
        )

        val authAccount = provider.findUser("test", authentication)
        assertNotNull(authAccount) {
            assertEquals(tempAccount, it.account)
        }
    }

    @Test
    fun `LDAP authentication success, LDAP detail with no email`() {
        whenever(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)

        val ldapUserDetails = mockExtendedLDAPUserDetails(
                email = ""
        )

        val authentication = mock<UsernamePasswordAuthenticationToken>()
        whenever(authentication.name).thenReturn("test")
        whenever(authentication.isAuthenticated).thenReturn(true)
        whenever(authentication.principal).thenReturn(ldapUserDetails)
        whenever(ldapAuthenticationProvider.authenticate(authentication)).thenReturn(authentication)

        val tempAccount = Account.of(
                "test",
                "Test user",
                "",
                SecurityRole.USER,
                ldapAuthenticationSourceProvider.source
        )
        whenever(accountService.findUserByNameAndSource("test", ldapAuthenticationSourceProvider)).thenReturn(
                Optional.empty()
        )

        val authAccount = provider.findUser("test", authentication)
        assertNotNull(authAccount) {
            assertEquals(tempAccount, it.account)
        }
    }

    @Test
    fun `LDAP authentication success, full LDAP detail`() {
        whenever(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)

        val ldapUserDetails = mockExtendedLDAPUserDetails(
                email = "test@test.com"
        )

        val authentication = mock<UsernamePasswordAuthenticationToken>()
        whenever(authentication.name).thenReturn("test")
        whenever(authentication.isAuthenticated).thenReturn(true)
        whenever(authentication.principal).thenReturn(ldapUserDetails)
        whenever(ldapAuthenticationProvider.authenticate(authentication)).thenReturn(authentication)

        val account = Account.of(
                "test",
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                ldapAuthenticationSourceProvider.source
        ).withId(ID.of(10))

        whenever(accountService.create(
                AccountInput(
                        "test",
                        "Test user",
                        "test@test.com",
                        "",
                        emptyList()
                ),
                "ldap"
        )).thenReturn(account)

        whenever(accountService.findUserByNameAndSource("test", ldapAuthenticationSourceProvider)).thenReturn(
                Optional.empty()
        )

        val authAccount = provider.findUser("test", authentication)
        assertNotNull(authAccount) {
            assertEquals(account, it.account)
        }
    }

    private fun mockExtendedLDAPUserDetails(email: String): ExtendedLDAPUserDetails {
        val support = mock<LdapUserDetails>()
        return ExtendedLDAPUserDetails(
                support = support,
                fullName = "Test user",
                email = email,
                groups = emptyList()
        )
    }

}
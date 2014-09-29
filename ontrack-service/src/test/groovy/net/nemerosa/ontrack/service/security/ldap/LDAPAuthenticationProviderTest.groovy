package net.nemerosa.ontrack.service.security.ldap

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.repository.AccountRepository
import org.junit.Before
import org.junit.Test
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider

import java.util.function.Supplier

import static org.mockito.Matchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class LDAPAuthenticationProviderTest {

    LDAPAuthenticationProvider provider
    LDAPProviderFactory ldapProviderFactory
    LdapAuthenticationProvider ldapAuthenticationProvider
    LDAPAuthenticationSourceProvider ldapAuthenticationSourceProvider
    AccountRepository accountRepository
    SecurityService securityService

    @Before
    void before() {
        AccountService accountService = mock(AccountService)
        ldapProviderFactory = mock(LDAPProviderFactory)
        ldapAuthenticationSourceProvider = new LDAPAuthenticationSourceProvider()
        accountRepository = mock(AccountRepository)
        securityService = mock(SecurityService)
        ldapAuthenticationProvider = mock(LdapAuthenticationProvider)
        ApplicationLogService applicationLogService = mock(ApplicationLogService)
        provider = new LDAPAuthenticationProvider(
                accountService,
                ldapProviderFactory,
                ldapAuthenticationSourceProvider,
                accountRepository,
                securityService,
                applicationLogService
        )
    }

    @Test
    void 'Cannot authenticate when LDAP is disabled'() {
        assert !provider.findUser("test", new UsernamePasswordAuthenticationToken("test", "xxx")).present
    }

    @Test
    void 'LDAP authentication failure: no authentication returned'() {
        when(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)
        assert !provider.findUser("test", new UsernamePasswordAuthenticationToken("test", "xxx")).present
    }

    @Test
    void 'LDAP authentication failure: partial authentication returned'() {
        when(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)
        def authentication = mock(UsernamePasswordAuthenticationToken)
        when(authentication.isAuthenticated()).thenReturn(false)
        when(ldapAuthenticationProvider.authenticate(authentication)).thenReturn(authentication)
        assert !provider.findUser("test", authentication).present
    }

    @Test
    void 'LDAP authentication success and existing account'() {
        when(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)

        def authentication = mock(UsernamePasswordAuthenticationToken)
        when(authentication.getName()).thenReturn("test")
        when(authentication.isAuthenticated()).thenReturn(true)
        when(ldapAuthenticationProvider.authenticate(authentication)).thenReturn(authentication)


        def account = Account.of(
                "test",
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                ldapAuthenticationSourceProvider.source
        )
        when(accountRepository.findUserByNameAndSource("test", ldapAuthenticationSourceProvider)).thenReturn(
                Optional.of(
                        account
                )
        )

        def authAccount = provider.findUser("test", authentication)
        assert authAccount.present
        assert authAccount.get() == account
    }

    @Test
    void 'LDAP authentication success, no LDAP detail'() {
        when(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)

        def authentication = mock(UsernamePasswordAuthenticationToken)
        when(authentication.getName()).thenReturn("test")
        when(authentication.isAuthenticated()).thenReturn(true)
        when(authentication.principal).thenReturn("No custom")
        when(ldapAuthenticationProvider.authenticate(authentication)).thenReturn(authentication)


        def tempAccount = Account.of(
                "test",
                "test",
                "",
                SecurityRole.USER,
                ldapAuthenticationSourceProvider.source
        )
        when(accountRepository.findUserByNameAndSource("test", ldapAuthenticationSourceProvider)).thenReturn(
                Optional.empty()
        )

        def authAccount = provider.findUser("test", authentication)
        assert authAccount.present
        assert authAccount.get() == tempAccount
    }

    @Test
    void 'LDAP authentication success, LDAP detail with no email'() {
        when(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)

        ExtendedLDAPUserDetails ldapUserDetails = mock(ExtendedLDAPUserDetails)
        when(ldapUserDetails.getFullName()).thenReturn("Test user")
        when(ldapUserDetails.getEmail()).thenReturn("")

        def authentication = mock(UsernamePasswordAuthenticationToken)
        when(authentication.getName()).thenReturn("test")
        when(authentication.isAuthenticated()).thenReturn(true)
        when(authentication.principal).thenReturn(ldapUserDetails)
        when(ldapAuthenticationProvider.authenticate(authentication)).thenReturn(authentication)


        def tempAccount = Account.of(
                "test",
                "Test user",
                "",
                SecurityRole.USER,
                ldapAuthenticationSourceProvider.source
        )
        when(accountRepository.findUserByNameAndSource("test", ldapAuthenticationSourceProvider)).thenReturn(
                Optional.empty()
        )

        def authAccount = provider.findUser("test", authentication)
        assert authAccount.present
        assert authAccount.get() == tempAccount
    }

    @Test
    void 'LDAP authentication success, full LDAP detail'() {
        when(ldapProviderFactory.provider).thenReturn(ldapAuthenticationProvider)

        ExtendedLDAPUserDetails ldapUserDetails = mock(ExtendedLDAPUserDetails)
        when(ldapUserDetails.getFullName()).thenReturn("Test user")
        when(ldapUserDetails.getEmail()).thenReturn("test@test.com")

        def authentication = mock(UsernamePasswordAuthenticationToken)
        when(authentication.getName()).thenReturn("test")
        when(authentication.isAuthenticated()).thenReturn(true)
        when(authentication.principal).thenReturn(ldapUserDetails)
        when(ldapAuthenticationProvider.authenticate(authentication)).thenReturn(authentication)

        when(securityService.asAdmin(any(Supplier) as Supplier)).then(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                Supplier<?> supplier = invocation.arguments[0] as Supplier<?>
                return supplier.get()
            }
        })
        def account = Account.of(
                "test",
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                ldapAuthenticationSourceProvider.source
        ).withId(ID.of(10))
        when(accountRepository.newAccount(
                Account.of(
                        "test",
                        "Test user",
                        "test@test.com",
                        SecurityRole.USER,
                        ldapAuthenticationSourceProvider.getSource()
                )
        )).thenReturn(account)

        when(accountRepository.findUserByNameAndSource("test", ldapAuthenticationSourceProvider)).thenReturn(
                Optional.empty()
        )

        def authAccount = provider.findUser("test", authentication)
        assert authAccount.present
        assert authAccount.get() == account
    }

}
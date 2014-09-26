package net.nemerosa.ontrack.service.security.ldap

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.settings.LDAPSettings
import net.nemerosa.ontrack.model.settings.SettingsService
import net.nemerosa.ontrack.repository.AccountRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class LDAPAuthenticationProviderIT extends AbstractServiceTestSupport {

    /**
     * Known names in the test LDAP.
     *
     * All passwords are set to `verysecret`
     */
    private static final String USER1 = 'user1'
    private static final String USER2 = 'user2'
    private static final String USER3 = 'user3'

    @Autowired
    private SettingsService settingsService

    @Autowired
    private AccountService accountService

    @Autowired
    private AccountRepository accountRepository

    @Autowired
    private LDAPAuthenticationProvider ldapAuthenticationProvider

    @Autowired
    private LDAPAuthenticationSourceProvider ldapAuthenticationSourceProvider

    @Before
    void 'Setting-up a LDAP'() {
        asUser().with(GlobalSettings).call({
            settingsService.saveLDAPSettings(
                    new LDAPSettings(
                            true,
                            'ldap://localhost:389/dc=nemerosa,dc=net',
                            '',
                            '',
                            '',
                            '',
                            'fullName',
                            'email'
                    )
            )
        })
    }

    @After
    void 'Unconfiguring the LDAP'() {
        asUser().with(GlobalSettings).call({
            settingsService.saveLDAPSettings(
                    LDAPSettings.NONE
            )
        })
    }

    @Test
    void 'Authenticating an existing account with the LDAP'() {
        // Creates an account
        def name = USER1
        def account = accountRepository.newAccount(
                Account.of(
                        name,
                        "User 1",
                        "$name@test.com",
                        SecurityRole.USER,
                        ldapAuthenticationSourceProvider.source
                )
        )
        // Authenticates this user
        def authenticatedAccount = ldapAuthenticationProvider.findUser(name, new UsernamePasswordAuthenticationToken(name, 'verysecret'))
        assert authenticatedAccount.present
        assert authenticatedAccount.get() == account
    }

    @Test
    void 'Authenticating with the LDAP and creating the account on the fly'() {
        def name = USER2
        def authenticatedAccount = ldapAuthenticationProvider.findUser(name, new UsernamePasswordAuthenticationToken(name, 'verysecret'))
        assert authenticatedAccount.present
        def account = authenticatedAccount.get()
        assert account.id.value > 0
        assert account.name == name
        assert account.fullName == "User 2"
        assert account.email == "user2@test.com"
    }

    @Test
    void 'Authenticating with the LDAP and missing email'() {
        def name = USER3
        def authenticatedAccount = ldapAuthenticationProvider.findUser(name, new UsernamePasswordAuthenticationToken(name, 'verysecret'))
        assert authenticatedAccount.present
        def account = authenticatedAccount.get()
        assert account.id.value == 0
        assert account.name == name
        assert account.fullName == name
        assert account.email == ""
    }

}

package net.nemerosa.ontrack.service.security.ldap

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.settings.LDAPSettings
import net.nemerosa.ontrack.model.settings.SettingsService
import net.nemerosa.ontrack.repository.AccountRepository
import net.nemerosa.ontrack.test.TestUtils
import org.junit.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.ldap.server.ApacheDSContainer

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
                            "ldap://localhost:${serverPort}",
                            'dc=nemerosa,dc=net',
                            'uid={0}',
                            '',
                            '',
                            '',
                            ''
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
        assert account.fullName == "User 3"
        assert account.email == ""
    }

    private static ApacheDSContainer server;
    private static int serverPort

    @BeforeClass
    static void 'Creating LDAP server'() {
        server = new ApacheDSContainer("dc=nemerosa,dc=net", "classpath:test-ldap.ldif")
        int port = TestUtils.availablePort
        server.port = port
        server.afterPropertiesSet()
        serverPort = port
        // Waiting a bit
        Thread.sleep 1000
    }

    @AfterClass
    static void 'Shutting down LDAP server'() {
        if (server != null) {
            server.stop()
            server = null
        }
    }

}

package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.extension.ldap.support.UnboundIdContainer
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.AccountInput
import net.nemerosa.ontrack.model.security.ProvidedGroupsService
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Integration tests for authentication w/ LDAP.
 */
class LDAPAuthenticationIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var authenticationProvider: LDAPCachedAuthenticationProvider

    @Autowired
    private lateinit var providedGroupsService: ProvidedGroupsService

    @After
    fun after() {
        asAdmin { settingsManagerService.saveSettings(LDAPSettings.NONE) }
    }

    @Test
    fun `Not using the LDAP when not configured`() {
        val user = authenticationProvider.findUser(ADMIN_USER, UsernamePasswordAuthenticationToken(ADMIN_USER, ADMIN_PASSWORD))
        assertNull(user, "LDAP not enabled")
    }

    @Test
    fun `Unknown account`() {
        setLDAPSettings()
        val user = authenticationProvider.findUser("xxxx", UsernamePasswordAuthenticationToken("xxxx", ADMIN_PASSWORD))
        assertNull(user, "Cannot find user")
    }

    @Test
    fun `Wrong password`() {
        setLDAPSettings()
        val user = authenticationProvider.findUser(ADMIN_USER, UsernamePasswordAuthenticationToken(ADMIN_USER, "xxxx"))
        assertNull(user, "Wrong user password")
    }

    @Test
    fun `Missing email`() {
        setLDAPSettings()
        assertFailsWith<LDAPEmailRequiredException> {
            authenticationProvider.findUser("gollum", UsernamePasswordAuthenticationToken("gollum", "password"))
        }
    }

    @Test
    fun `Creation of new account`() {
        setLDAPSettings()
        deleteAccount(ADMIN_USER)
        val user = authenticationProvider.findUser(ADMIN_USER, UsernamePasswordAuthenticationToken(ADMIN_USER, ADMIN_PASSWORD))
        assertNotNull(user) {
            val id = it.account.id()
            assertEquals(LDAPAuthenticationSourceProvider.SOURCE.id, it.account.authenticationSource.id)
            assertEquals(ADMIN_USER, it.username)
            assertEquals("", it.password)
            assertEquals("Damien Coraboeuf", it.account.fullName)
            // Groups
            asAdmin {
                val groups = providedGroupsService.getProvidedGroups(id, LDAPAuthenticationSourceProvider.SOURCE)
                assertEquals(
                        setOf("admin", "user"),
                        groups
                )
            }
        }
    }

    @Test
    fun `Login a second time`() {
        setLDAPSettings()
        deleteAccount("bilbo")
        val user = authenticationProvider.findUser("bilbo", UsernamePasswordAuthenticationToken("bilbo", "password"))
        assertNotNull(user) {
            val id = it.account.id()
            // Login a second time
            val second = authenticationProvider.findUser("bilbo", UsernamePasswordAuthenticationToken("bilbo", "password"))
            // Checks the user is the same
            assertNotNull(second) { s ->
                assertEquals(id, s.account.id())
            }
            // Groups
            asAdmin {
                val groups = providedGroupsService.getProvidedGroups(id, LDAPAuthenticationSourceProvider.SOURCE)
                assertEquals(
                        setOf("user"),
                        groups
                )
            }
        }
    }

    @Test
    fun `Cannot override a non LDAP account`() {
        setLDAPSettings()
        // Makes sure the account with name `sauron` is built-in
        asAdmin {
            deleteAccount("sauron")
            accountService.create(
                    AccountInput(
                            name = "sauron",
                            fullName = "The enemy",
                            email = "sauron@mordor.com",
                            password = "the-ring",
                            groups = emptyList()
                    )
            )
        }
        // Logs and fails
        assertFailsWith<LDAPNotALDAPAccountException> {
            authenticationProvider.findUser("sauron", UsernamePasswordAuthenticationToken("sauron", "password"))
        }
    }

    private fun deleteAccount(name: String) {
        asAdmin {
            val account = accountService.findAccountByName(name)
            if (account != null) {
                accountService.deleteAccount(account.id)
            }
        }
    }

    private fun setLDAPSettings() {
        asAdmin { settingsManagerService.saveSettings(ldapSettings) }
    }

    private val ldapSettings
        get() = LDAPSettings(
                isEnabled = true,
                url = "ldap://localhost:${ldapContainer.port}",
                user = "uid=damien.coraboeuf,ou=people,dc=nemerosa,dc=net",
                password = "admin",
                searchBase = "dc=nemerosa,dc=net",
                searchFilter = "(uid={0})",
                groupSearchBase = "ou=groups,dc=nemerosa,dc=net",
                groupSearchFilter = "(member={0})"
        )

    companion object {

        /**
         * Known account for admin group
         */
        private const val ADMIN_USER = "damien.coraboeuf"

        /**
         * Known account password for admin group
         */
        private const val ADMIN_PASSWORD = "admin"

        private lateinit var ldapContainer: UnboundIdContainer

        @BeforeClass
        @JvmStatic
        fun start() {
            val ldif = LDAPAuthenticationIT::class.java.getResourceAsStream("/users.ldif").reader().readText()
            ldapContainer = UnboundIdContainer("dc=nemerosa,dc=net", ldif)
            ldapContainer.start()
        }

        @AfterClass
        @JvmStatic
        fun stop() {
            if (this::ldapContainer.isInitialized) {
                ldapContainer.stop()
            }
        }

    }

}
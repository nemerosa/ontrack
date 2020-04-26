package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.extension.ldap.support.UnboundIdContainer
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Integration tests for authentication w/ LDAP.
 */
class LDAPAuthenticationIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var authenticationProvider: LDAPCachedAuthenticationProvider

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
    fun `Creation of new account`() {
        setLDAPSettings()
        val user = authenticationProvider.findUser(ADMIN_USER, UsernamePasswordAuthenticationToken(ADMIN_USER, ADMIN_PASSWORD))
        assertNotNull(user) {
            assertEquals(LDAPAuthenticationSource.id, it.account.authenticationSource.id)
            assertEquals(ADMIN_USER, it.username)
            assertEquals("", it.password)
            assertEquals("Damien Coraboeuf", it.account.fullName)
        }
    }

    private fun setLDAPSettings() {
        asAdmin { settingsManagerService.saveSettings(ldapSettings) }
    }

    private val ldapSettings
        get() = LDAPSettings(
                isEnabled = true,
                url = "ldap://localhost:${ldapContainer.port}",
                user = "uid=admin,ou=people,dc=nemerosa,dc=net",
                password = "admin",
                searchBase = "dc=nemerosa,dc=net",
                searchFilter = "(uid={0})"
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
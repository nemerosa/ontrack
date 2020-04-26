package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.extension.ldap.support.UnboundIdContainer
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import kotlin.test.assertNull

/**
 * Integration tests for authentication w/ LDAP.
 */
class LDAPAuthenticationIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var authenticationProvider: LDAPCachedAuthenticationProvider

    @Configuration
    class LDAPAuthenticationITConfiguration {
        @Bean
        fun ldapContainer(): UnboundIdContainer {
            return UnboundIdContainer("dc=nemerosa,dc=net", "classpath:users.ldif")
        }
    }

    @Test
    fun `Not using the LDAP when not configured`() {
        val user = authenticationProvider.findUser(ADMIN_USER, UsernamePasswordAuthenticationToken(ADMIN_USER, ADMIN_PASSWORD))
        assertNull(user, "LDAP not enabled")
    }

    companion object {

        /**
         * Known account for admin group
         */
        private const val ADMIN_USER = "damien.coraboeuf"

        /**
         * Known account password for admin group
         */
        private const val ADMIN_PASSWORD = "admin"

    }

}
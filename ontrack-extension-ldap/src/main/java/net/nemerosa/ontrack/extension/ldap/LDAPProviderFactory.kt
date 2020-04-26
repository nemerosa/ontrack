package net.nemerosa.ontrack.extension.ldap

import org.springframework.security.ldap.authentication.LdapAuthenticationProvider

/**
 * Facade to get an actual LDAP authentication provider, based on checking if the
 * LDAP settings are enabled or not.
 */
interface LDAPProviderFactory {

    /**
     * Gets a configured LDAP authentication provider
     */
    val provider: LdapAuthenticationProvider?

    /**
     * Invalidates any configured LDAP authentication provider
     */
    fun invalidate()

}
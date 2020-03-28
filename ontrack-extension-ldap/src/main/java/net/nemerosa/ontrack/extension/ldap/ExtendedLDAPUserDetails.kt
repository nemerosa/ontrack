package net.nemerosa.ontrack.extension.ldap

import org.springframework.security.ldap.userdetails.LdapUserDetails

open class ExtendedLDAPUserDetails(
        private val support: LdapUserDetails,
        val fullName: String,
        val email: String,
        val groups: Collection<String>
) : LdapUserDetails by support

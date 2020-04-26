package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser

class LDAPOntrackAuthenticatedUser(
        private val support: OntrackAuthenticatedUser,
        private val ldapDetails: ExtendedLDAPUserDetails
) : OntrackAuthenticatedUser by support {

    /**
     * LDAP groups
     */
    val ldapGroups: Collection<String> get() = ldapDetails.groups

}
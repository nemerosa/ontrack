package net.nemerosa.ontrack.extension.ldap

import org.apache.commons.lang3.StringUtils
import org.springframework.ldap.core.ContextSource
import org.springframework.ldap.core.DirContextOperations
import org.springframework.ldap.support.LdapUtils
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.SpringSecurityLdapTemplate
import org.springframework.security.ldap.userdetails.LdapUserDetails
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper
import java.util.*
import javax.naming.ldap.LdapName

open class ConfigurableUserDetailsContextMapper(
        private val settings: LDAPSettings,
        private val ldapTemplate: SpringSecurityLdapTemplate
) : LdapUserDetailsMapper() {

    internal constructor(settings: LDAPSettings, contextSource: ContextSource) : this(
            settings,
            SpringSecurityLdapTemplate(contextSource)
    )

    override fun mapUserFromContext(ctx: DirContextOperations, username: String, authorities: Collection<GrantedAuthority?>): UserDetails { // Default details
        val userDetails = super.mapUserFromContext(ctx, username, authorities) as LdapUserDetails
        return extendUserDetails(ctx, userDetails)
    }

    fun extendUserDetails(ctx: DirContextOperations, userDetails: LdapUserDetails): UserDetails {
        // Full name
        var fullNameAttribute = settings.fullNameAttribute
        if (StringUtils.isBlank(fullNameAttribute)) {
            fullNameAttribute = "cn"
        }
        val fullName = ctx.getStringAttribute(fullNameAttribute) ?: userDetails.username
        // Email
        var emailAttribute = settings.emailAttribute
        if (StringUtils.isBlank(emailAttribute)) {
            emailAttribute = "email"
        }
        val email = ctx.getStringAttribute(emailAttribute) ?: ""
        // Groups
        val parsedGroups: MutableSet<String> = HashSet()
        // ... from the user
        parsedGroups.addAll(getGroupsFromUser(ctx))
        // ... from the groups
        parsedGroups.addAll(getGroups(userDetails))
        // OK
        return ExtendedLDAPUserDetails(userDetails, fullName, email, parsedGroups)
    }

    private fun getGroups(userDetails: LdapUserDetails): Collection<String> {
        val groupSearchBase = settings.groupSearchBase
        return if (StringUtils.isNotBlank(groupSearchBase)) {
            var groupSearchFilter = settings.groupSearchFilter
            if (StringUtils.isBlank(groupSearchFilter)) {
                groupSearchFilter = "(member={0})"
            }
            var groupNameAttribute = settings.groupNameAttribute
            if (StringUtils.isBlank(groupNameAttribute)) {
                groupNameAttribute = "cn"
            }
            ldapTemplate.searchForSingleAttributeValues(
                    groupSearchBase,
                    groupSearchFilter, arrayOf(userDetails.dn),
                    groupNameAttribute
            )
        } else {
            emptySet()
        }
    }

    private fun getGroupsFromUser(ctx: DirContextOperations): Collection<String> {
        val groupNameAttribute: String = if (settings.groupNameAttribute.isNullOrBlank()) {
            "cn"
        } else {
            settings.groupNameAttribute
        }
        var groupAttribute = settings.groupAttribute
        if (StringUtils.isBlank(groupAttribute)) {
            groupAttribute = "memberOf"
        }
        val groupFilter = settings.groupFilter
        val groups = ctx.getStringAttributes(groupAttribute)
        return if (groups != null && groups.isNotEmpty()) {
            groups
                    // Parsing of the group
                    .map { distinguishedName: String -> LdapUtils.newLdapName(distinguishedName) }
                    // Filter on OU
                    .filter { dn: LdapName ->
                        val ou = getValue(dn, "OU")
                        groupFilter.isNullOrBlank() || StringUtils.equalsIgnoreCase(ou, groupFilter)
                    }
                    // Getting the common name
                    .mapNotNull { dn: LdapName -> getValue(dn, groupNameAttribute) }
                    // Keeps only the groups being filled in
                    .filter { cs: String? -> !cs.isNullOrBlank() }
                    // As a set
                    .toSet()
        } else {
            emptySet()
        }
    }

    companion object {
        internal fun getValue(dn: LdapName, key: String): String? {
            return try {
                LdapUtils.getStringValue(dn, StringUtils.upperCase(key))
            } catch (ignored: IllegalArgumentException) {
                try {
                    LdapUtils.getStringValue(dn, StringUtils.lowerCase(key))
                } catch (ignored2: IllegalArgumentException) {
                    null
                } catch (ignored2: NoSuchElementException) {
                    null
                }
            } catch (ignored: NoSuchElementException) {
                try {
                    LdapUtils.getStringValue(dn, StringUtils.lowerCase(key))
                } catch (ignored2: IllegalArgumentException) {
                    null
                } catch (ignored2: NoSuchElementException) {
                    null
                }
            }
        }
    }

}
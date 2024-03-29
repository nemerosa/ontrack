package net.nemerosa.ontrack.extension.ldap

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("LDAP settings")
data class LDAPSettings(
        @get:JsonProperty("enabled")
        @APIDescription("Is LDAP authentication enabled?")
        val isEnabled: Boolean,
        val url: String = "",
        val searchBase: String = "",
        val searchFilter: String = "",
        val user: String = "",
        val password: String = "",
        val fullNameAttribute: String? = "",
        val emailAttribute: String? = "",
        val groupAttribute: String? = "",
        val groupFilter: String? = "",
        /**
         * The ID of the attribute which contains the name for a group
         */
        val groupNameAttribute: String? = DEFAULT_GROUP_NAME_ATTRIBUTE,
        /**
         * The base DN from which the search for group membership should be performed
         */
        val groupSearchBase: String? = DEFAULT_GROUP_SEARCH_BASE,
        /**
         * The pattern to be used for the user search. {0} is the user's DN
         */
        val groupSearchFilter: String? = DEFAULT_GROUP_SEARCH_FILTER
) {

    fun withPassword(password: String): LDAPSettings {
        return if (this.password == password) this else LDAPSettings(isEnabled, url, searchBase, searchFilter, user, password, fullNameAttribute, emailAttribute, groupAttribute, groupFilter, groupNameAttribute, groupSearchBase, groupSearchFilter)
    }

    fun withFullNameAttribute(fullNameAttribute: String): LDAPSettings {
        return if (this.fullNameAttribute == fullNameAttribute) this else LDAPSettings(isEnabled, url, searchBase, searchFilter, user, password, fullNameAttribute, emailAttribute, groupAttribute, groupFilter, groupNameAttribute, groupSearchBase, groupSearchFilter)
    }

    fun withEmailAttribute(emailAttribute: String): LDAPSettings {
        return if (this.emailAttribute == emailAttribute) this else LDAPSettings(isEnabled, url, searchBase, searchFilter, user, password, fullNameAttribute, emailAttribute, groupAttribute, groupFilter, groupNameAttribute, groupSearchBase, groupSearchFilter)
    }

    fun withGroupAttribute(groupAttribute: String): LDAPSettings {
        return if (this.groupAttribute == groupAttribute) this else LDAPSettings(isEnabled, url, searchBase, searchFilter, user, password, fullNameAttribute, emailAttribute, groupAttribute, groupFilter, groupNameAttribute, groupSearchBase, groupSearchFilter)
    }

    fun withGroupFilter(groupFilter: String): LDAPSettings {
        return if (this.groupFilter == groupFilter) this else LDAPSettings(isEnabled, url, searchBase, searchFilter, user, password, fullNameAttribute, emailAttribute, groupAttribute, groupFilter, groupNameAttribute, groupSearchBase, groupSearchFilter)
    }

    fun withGroupNameAttribute(groupNameAttribute: String): LDAPSettings {
        return if (this.groupNameAttribute == groupNameAttribute) this else LDAPSettings(isEnabled, url, searchBase, searchFilter, user, password, fullNameAttribute, emailAttribute, groupAttribute, groupFilter, groupNameAttribute, groupSearchBase, groupSearchFilter)
    }

    fun withGroupSearchBase(groupSearchBase: String): LDAPSettings {
        return if (this.groupSearchBase == groupSearchBase) this else LDAPSettings(isEnabled, url, searchBase, searchFilter, user, password, fullNameAttribute, emailAttribute, groupAttribute, groupFilter, groupNameAttribute, groupSearchBase, groupSearchFilter)
    }

    fun withGroupSearchFilter(groupSearchFilter: String): LDAPSettings {
        return if (this.groupSearchFilter == groupSearchFilter) this else LDAPSettings(isEnabled, url, searchBase, searchFilter, user, password, fullNameAttribute, emailAttribute, groupAttribute, groupFilter, groupNameAttribute, groupSearchBase, groupSearchFilter)
    }

    companion object {
        val NONE = LDAPSettings(
                isEnabled = false,
                url = "",
                searchBase = "",
                searchFilter = "",
                user = "",
                password = "",
                fullNameAttribute = "",
                emailAttribute = "",
                groupAttribute = "",
                groupFilter = "",
                groupNameAttribute = "",
                groupSearchBase = "",
                groupSearchFilter = ""
        )

        const val DEFAULT_GROUP_NAME_ATTRIBUTE = "cn"
        const val DEFAULT_GROUP_SEARCH_BASE = ""
        const val DEFAULT_GROUP_SEARCH_FILTER = "(member={0})"
    }

}
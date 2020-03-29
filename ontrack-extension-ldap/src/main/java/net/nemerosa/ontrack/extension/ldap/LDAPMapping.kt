package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AccountGroupMapping
import net.nemerosa.ontrack.model.structure.ID

class LDAPMapping(
        val id: ID,
        val type: String,
        val name: String,
        val group: AccountGroup
) {
    companion object {
        @JvmStatic
        fun of(mapping: AccountGroupMapping): LDAPMapping = LDAPMapping(
                mapping.id,
                mapping.type,
                mapping.name,
                mapping.group
        )
    }
}
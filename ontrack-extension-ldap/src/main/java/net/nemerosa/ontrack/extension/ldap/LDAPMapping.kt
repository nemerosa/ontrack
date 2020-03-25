package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AccountGroupMapping
import net.nemerosa.ontrack.model.structure.ID

class LDAPMapping(id: ID, type: String, name: String, group: AccountGroup) : AccountGroupMapping(id, type, name, group) {
    companion object {
        @JvmStatic
        fun of(mapping: AccountGroupMapping): LDAPMapping {
            return LDAPMapping(
                    mapping.id,
                    mapping.type,
                    mapping.name,
                    mapping.group
            )
        }
    }
}
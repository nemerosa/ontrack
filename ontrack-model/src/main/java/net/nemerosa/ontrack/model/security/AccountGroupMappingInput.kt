package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.ID

/**
 * Defines a mapping between a provided group (by a LDAP system for example)
 * and an actual Ontrack [group][AccountGroup].
 *
 * @property name Name of the provided group
 * @property group ID of the [AccountGroup]
 */
data class AccountGroupMappingInput(val name: String, val group: ID)
